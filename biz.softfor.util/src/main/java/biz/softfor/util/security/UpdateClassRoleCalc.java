package biz.softfor.util.security;

public class UpdateClassRoleCalc extends UpdateRoleCalc {

  private final Class<?> o;

  public UpdateClassRoleCalc(Class<?> v) {
    super(RoleCalc.CLASS);
    o = v;
  }

  @Override
  public UpdateAccess annotation() {
    return o.getAnnotation(UpdateAccess.class);
  }

  @Override
  public String defaultDescription() {
    return new ClassRoleCalc(o).description();
  }

  @Override
  public String defaultName() {
    return new ClassRoleCalc(o).name();
  }

  @Override
  public boolean ignore() {
    return o.isAnnotationPresent(IgnoreAccess.class);
  }

  @Override
  public boolean isUrl() {
    return false;
  }

  @Override
  public String objName() {
    return o.getName();
  }

}
