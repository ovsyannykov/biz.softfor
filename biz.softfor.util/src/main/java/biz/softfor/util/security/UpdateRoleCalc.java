package biz.softfor.util.security;

import java.text.MessageFormat;
import org.apache.commons.lang3.StringUtils;

public abstract class UpdateRoleCalc extends AbstractRoleCalc {

  public final static String NAME_TPL = "{0} (update)";

  protected UpdateRoleCalc(int typ) {
    super(true, typ, NAME_TPL);
  }

  public abstract UpdateAccess annotation();
  public abstract String defaultName();
  public abstract String defaultDescription();

  @Override
  public DefaultAccess defaultAccess() {
    UpdateAccess a = annotation();
    return a == null ? DefaultAccess.EVERYBODY : a.defaultAccess();
  }

  @Override
  public boolean deniedForAll() {
    UpdateAccess a = annotation();
    return a == null ? false : a.deniedForAll();
  }

  @Override
  public String description() {
    String result = null;
    UpdateAccess a = annotation();
    if(a != null) {
      result = a.description();
    }
    if(StringUtils.isBlank(result)) {
      result = MessageFormat.format(nameFormat, defaultDescription());
    }
    return result;
  }

  @Override
  public String name() {
    String result = null;
    UpdateAccess a = annotation();
    if(a != null) {
      result = a.value();
    }
    if(StringUtils.isBlank(result)) {
      result = MessageFormat.format(nameFormat, defaultName());
    }
    return result;
  }

}
