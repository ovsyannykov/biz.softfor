package biz.softfor.user.spring;

import biz.softfor.util.security.DefaultAccess;
import java.util.HashSet;
import java.util.Set;
import lombok.ToString;

@ToString
public class RoleAccess {

  public DefaultAccess defaultAccess;
  public final Set<String> groups;

  public RoleAccess(DefaultAccess defaultAccess, Set<String> groups) {
    this.defaultAccess = defaultAccess;
    this.groups = new HashSet(groups);
  }

  public RoleAccess(RoleAccess roleAccess) {
    this(roleAccess.defaultAccess, new HashSet(roleAccess.groups));
  }

  public RoleAccess() {
    this(DefaultAccess.EVERYBODY, new HashSet());
  }

}
