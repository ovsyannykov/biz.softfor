package biz.softfor.util.security;

import biz.softfor.util.StringUtil;

public class ClassRoleCalc extends RoleCalc {

  private final Class<?> o;

  public ClassRoleCalc(Class<?> v) {
    super(CLASS);
    o = v;
  }

  @Override
  public ActionAccess annotation() {
    return o.getAnnotation(ActionAccess.class);
  }

  @Override
  public String defaultDescription() {
    return o.getPackageName() + " " + defaultName();
  }

  @Override
  public String defaultName() {
    return StringUtil.camelCaseToSentenceCase(o.getSimpleName());
  }

  @Override
  public boolean ignore() {
    return o.isAnnotationPresent(IgnoreAccess.class);
  }

  @Override
  public String objName() {
    return o.getName();
  }

}
