package biz.softfor.spring.restcontrollergen;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PACKAGE)
@Retention(RetentionPolicy.SOURCE)
public @interface GenRestController {

  public Class<?>[] value() default {};
  public Class<?>[] exclude() default {};

}
