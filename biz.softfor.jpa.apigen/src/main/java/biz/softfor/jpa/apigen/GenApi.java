package biz.softfor.jpa.apigen;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PACKAGE)
@Retention(RetentionPolicy.SOURCE)
public @interface GenApi {

  public Class<?>[] value() default {};
  public Class<?>[] exclude() default {};
  public Class<?>[] restControllers() default {};

}
