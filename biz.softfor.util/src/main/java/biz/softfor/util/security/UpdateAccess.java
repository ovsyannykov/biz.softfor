package biz.softfor.util.security;

import biz.softfor.util.api.Identifiable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface UpdateAccess {

  /**label, name*/
  public String value() default "";

  public boolean deniedForAll() default false;

  public String description() default "";
  public final static String DESCRIPTION = ActionAccess.DESCRIPTION;

  public long id() default 0L;
  public final static String ID = Identifiable.ID;

  public DefaultAccess defaultAccess() default DefaultAccess.EVERYBODY;
  public final static String DEFAULT_ACCESS = ActionAccess.DEFAULT_ACCESS;

}
