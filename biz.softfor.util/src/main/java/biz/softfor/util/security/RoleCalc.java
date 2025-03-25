package biz.softfor.util.security;

import org.apache.commons.lang3.StringUtils;

  public abstract class RoleCalc extends AbstractRoleCalc {

  public abstract ActionAccess annotation();
  public abstract String defaultName();
  public abstract String defaultDescription();

  protected RoleCalc(int typ, String nameFormat) {
    super(false, typ, nameFormat);
  }

  protected RoleCalc(int typ) {
    super(false, typ, "{0}");
  }

  @Override
  public DefaultAccess defaultAccess() {
    ActionAccess a = annotation();
    return a == null ? DefaultAccess.EVERYBODY : a.defaultAccess();
  }

  @Override
  public boolean deniedForAll() {
    ActionAccess a = annotation();
    return a == null ? false : a.deniedForAll();
  }

  @Override
  public String description() {
    String result = null;
    ActionAccess a = annotation();
    if(a != null) {
      result = a.description();
    }
    if(StringUtils.isBlank(result)) {
      result = defaultDescription();
    }
    return result;
  }

  @Override
  public boolean isUrl() {
    return false;
  }

  @Override
  public String name() {
    String result = null;
    ActionAccess a = annotation();
    if(a != null) {
      result = a.value();
    }
    if(StringUtils.isBlank(result)) {
      result = defaultName();
    }
    return result;
  }

}
