package biz.softfor.spring.security;

import java.util.Collection;
import org.springframework.security.core.userdetails.User;

public class UserDetailsEx extends User {

  public final long id;

  public UserDetailsEx
  (Long id, String username, String password, Collection<String> roles) {
    super(username, password, SecurityUtil.authorities(roles));
    this.id = id;
  }

}
