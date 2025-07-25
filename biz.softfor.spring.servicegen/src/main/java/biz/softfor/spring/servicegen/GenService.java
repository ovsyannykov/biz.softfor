package biz.softfor.spring.servicegen;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PACKAGE)
@Retention(RetentionPolicy.SOURCE)
public @interface GenService {

  public Class<?>[] value() default {};
  public Class<?>[] exclude() default {};

}
