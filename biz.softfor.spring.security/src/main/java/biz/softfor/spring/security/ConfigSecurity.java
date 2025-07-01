package biz.softfor.spring.security;

import biz.softfor.spring.messagesi18n.I18n;
import biz.softfor.user.jpa.Role_;
import biz.softfor.user.jpa.User;
import biz.softfor.user.jpa.UserGroup_;
import biz.softfor.user.jpa.User_;
import biz.softfor.util.Constants;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
@EnableWebSecurity(debug = false)
@ComponentScan
public class ConfigSecurity {

  private final I18n i18n;

  public ConfigSecurity(I18n i18n) {
    this.i18n = i18n;
  }

  @Bean
  public AuthenticationManager authenticationManager
  (AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider
  (UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
    DaoAuthenticationProvider result
    = new DaoAuthenticationProvider(userDetailsService);
    result.setPasswordEncoder(passwordEncoder);
    return result;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public UserDetailsService userDetailsService
  (EntityManager em, PlatformTransactionManager tm) {
    return username -> {
      CriteriaBuilder cb = em.getCriteriaBuilder();
      CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
      Root<User> root = cq.from(User.class);
      cq.select(cb.tuple(
        root.get(User_.ID)//0
      , root.get(User_.PASSWORD)//1
      , root.join(Role_.GROUPS, JoinType.LEFT).get(UserGroup_.NAME)//2
      ));
      cq.where(cb.equal(root.get(User_.USERNAME), username));
      cq.orderBy(cb.asc(root.get(User_.ID)));
      List<Tuple> res = new TransactionTemplate(tm).execute
      (status -> em.createQuery(cq).getResultList());
      if(res == null || res.isEmpty()) {
        throw new UsernameNotFoundException
        (i18n.message(User.User_not_found, username));
      }
      Collection<String> roles = new ArrayList<>(res.size());
      for(Tuple t : res) {
        roles.add(Constants.ROLE_PREFIX + t.get(2, String.class));
      }
      Tuple t = res.get(0);
      return new UserDetailsEx
      (t.get(0, Long.class), username, t.get(1, String.class), roles);
    };
  }

}
