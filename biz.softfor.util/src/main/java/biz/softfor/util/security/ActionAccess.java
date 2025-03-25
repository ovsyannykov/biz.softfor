package biz.softfor.util.security;

import biz.softfor.util.api.Identifiable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ActionAccess {

  /**label, name*/
  public String value() default "";

  public String description() default "";
  public final static String DESCRIPTION = "description";

  public long id() default 0L;
  public final static String ID = Identifiable.ID;

  public boolean deniedForAll() default false;

  public DefaultAccess defaultAccess() default DefaultAccess.EVERYBODY;
  public final static String DEFAULT_ACCESS = "defaultAccess";

}
