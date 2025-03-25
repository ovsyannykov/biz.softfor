package biz.softfor.util.security;

import biz.softfor.util.StringUtil;
import java.lang.reflect.Field;
import org.apache.commons.lang3.ArrayUtils;

public class UpdateFieldRoleCalc extends UpdateRoleCalc {

  private final Class<?> p;
  private final Field o;

  public UpdateFieldRoleCalc(Class<?> parent, Field v) {
    super(RoleCalc.FIELD);
    p = parent;
    o = v;
  }

  @Override
  public UpdateAccess annotation() {
    return o.getAnnotation(UpdateAccess.class);
  }

  @Override
  public String defaultDescription() {
    return new FieldRoleCalc(p, o).description();
  }

  @Override
  public String defaultName() {
    return new FieldRoleCalc(p, o).name();
  }

  @Override
  public boolean ignore() {
    return ArrayUtils.contains(FieldRoleCalc.IGNORED, o.getName())
    || o.isAnnotationPresent(IgnoreAccess.class);
  }

  @Override
  public boolean isUrl() {
    return false;
  }

  @Override
  public String objName() {
    return StringUtil.field(p.getName(), o.getName());
  }

}
