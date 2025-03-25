package biz.softfor.spring.security;

import biz.softfor.util.Constants;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

  public static Collection<? extends GrantedAuthority> authorities
  (Collection<String> groups) {
    Collection<? extends GrantedAuthority> result;
    if(groups == null) {
      result = Collections.EMPTY_LIST;
    } else {
      result = groups.stream().map(SimpleGrantedAuthority::new)
      .collect(Collectors.toList());
    }
    return result;
  }

  public static Authentication getAuthentication() {
    return SecurityContextHolder.getContext().getAuthentication();
  }

  public static boolean isAuthorized(Authentication a) {
    return a != null && !(a instanceof AnonymousAuthenticationToken);
  }

  public static Collection<String> groups() {
    Authentication a = getAuthentication();
    return a == null ? Constants.ANONYMOUS_ROLES : groups(a.getAuthorities());
  }

  public static Collection<String> groups
  (Collection<? extends GrantedAuthority> authorities) {
    return authorities.stream().map(GrantedAuthority::getAuthority)
    .collect(Collectors.toList());
  }

  public static void updateAuthentication(Collection<String> groups) {
    Authentication auth = getAuthentication();
    Authentication newAuth = new UsernamePasswordAuthenticationToken
    (auth.getPrincipal(), auth.getCredentials(), authorities(groups));
    SecurityContextHolder.getContext().setAuthentication(newAuth);
  }

}
