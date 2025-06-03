package biz.softfor.util.security;

import biz.softfor.util.StringUtil;
import java.lang.reflect.Field;
import org.apache.commons.lang3.ArrayUtils;

public class UpdateFieldRoleCalc extends UpdateRoleCalc {

  private final Class<?> p;
  private final Field o;

  public UpdateFieldRoleCalc(Field v) {
    super(RoleCalc.FIELD);
    p = v.getDeclaringClass();
    o = v;
  }

  @Override
  public UpdateAccess annotation() {
    return o.getAnnotation(UpdateAccess.class);
  }

  @Override
  public String defaultDescription() {
    return p.getPackageName() + " " + defaultName();
  }

  @Override
  public String defaultName() {
    return
    StringUtil.camelCaseToSentenceCase(p.getSimpleName() + "#" + o.getName());
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
