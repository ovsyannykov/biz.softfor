package biz.softfor.util.security;

import biz.softfor.util.Reflection;
import biz.softfor.util.StringUtil;
import biz.softfor.util.api.Identifiable;
import java.lang.reflect.Field;
import org.apache.commons.lang3.ArrayUtils;

public class FieldRoleCalc extends RoleCalc {

  public final static String IGNORED[]
  = { Identifiable.ID, Reflection.HIBERNATE_PROXY };

  private final Class<?> p;
  private final Field o;

  public FieldRoleCalc(Class<?> parent, Field v) {
    super(FIELD);
    p = parent;
    o = v;
  }

  public FieldRoleCalc(Class<?> parent, String name) {
    this(parent, Reflection.declaredField(parent, name));
  }

  @Override
  public ActionAccess annotation() {
    return o.getAnnotation(ActionAccess.class);
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
    return ArrayUtils.contains(IGNORED, o.getName())
    || o.isAnnotationPresent(IgnoreAccess.class);
  }

  @Override
  public String objName() {
    return StringUtil.field(p.getName(), o.getName());
  }

}
