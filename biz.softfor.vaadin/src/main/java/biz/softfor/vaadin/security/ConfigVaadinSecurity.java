package biz.softfor.vaadin.security;

import biz.softfor.util.api.StdPath;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import java.time.Duration;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithms;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.stereotype.Component;

@Component
public class ConfigVaadinSecurity extends VaadinWebSecurity {

  @Value("${biz.softfor.spring.security.jwt.secret}")
  private String secret;
  @Value("${biz.softfor.spring.security.jwt.lifetime}")
  private Duration lifetime;
  @Value("${biz.softfor.spring.security.jwt.refreshLifetime}")
  private Duration refreshLifetime;
  @Value("${spring.application.name}")
  private String issuer;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
      .authorizeHttpRequests(auth -> auth
        .requestMatchers(
          PathPatternRequestMatcher.withDefaults().matcher
          (HttpMethod.GET, "/images/*.png")
        ).permitAll()
      )
    ;
    super.configure(http);
    setLoginView(http, LoginView.class);
    http.formLogin(formLogin -> {
      formLogin.loginPage(StdPath.LOGIN).permitAll();
      formLogin.successHandler(new LoginSuccessHandler());
    });

    SecretKey sk = new SecretKeySpec
    (Base64.getDecoder().decode(secret), JwsAlgorithms.HS256);
    setStatelessAuthentication(http, sk, issuer, lifetime.getSeconds());
  }

}
