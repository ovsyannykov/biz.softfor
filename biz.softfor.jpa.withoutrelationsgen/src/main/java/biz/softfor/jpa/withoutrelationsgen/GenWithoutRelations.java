package biz.softfor.jpa.withoutrelationsgen;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PACKAGE)
@Retention(RetentionPolicy.SOURCE)
public @interface GenWithoutRelations {

  public Class<?>[] value() default {};
  public Class<?>[] exclude() default {};

}
