package biz.softfor.util.security;

import biz.softfor.util.Reflection;
import biz.softfor.util.StringUtil;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import org.apache.commons.lang3.ArrayUtils;

public class MethodRoleCalc extends RoleCalc {

  public final static String SERVICE = "{0} (service)";

  protected final Class<?> p;
  protected final Method o;
  protected final String methodName;

  private final static String IGNORED[] = {
    "clone"
  , Reflection.EQUALS_METHOD
  , "finalize"
  , "getClass"
  , Reflection.HASHCODE_METHOD
  , "notify"
  , "notifyAll"
  , Reflection.TOSTRING_METHOD
  , "wait"
  };

  public MethodRoleCalc(Class<?> parent, Method v, String nameFormat) {
    super(METHOD, nameFormat);
    p = parent;
    o = v;
    methodName = o.getName();
  }

  public MethodRoleCalc(Class<?> parent, String methodName) {
    super(METHOD);
    p = parent;
    o = null;
    this.methodName = methodName;
  }

  @Override
  public ActionAccess annotation() {
    return o == null ? null : o.getAnnotation(ActionAccess.class);
  }

  @Override
  public String defaultDescription() {
    return p.getPackageName() + " " + defaultName();
  }

  @Override
  public String defaultName() {
    return MessageFormat.format(
      nameFormat
    , StringUtil.camelCaseToSentenceCase(p.getSimpleName() + ">" + methodName)
    );
  }

  @Override
  public boolean ignore() {
    return (o.getModifiers() & Modifier.STATIC) != 0
    || ArrayUtils.contains(IGNORED, o.getName())
    || o.isAnnotationPresent(IgnoreAccess.class);
  }

  @Override
  public String objName() {
    return StringUtil.field(p.getName(), methodName);
  }

}
