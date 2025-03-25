package biz.softfor.util;

import biz.softfor.util.api.ServerError;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.java.Log;
import org.junit.jupiter.api.Test;

@Log
public class ReflectionTest {

  @Test
  public void getClassesTest() {
    List<Class> classes = new ArrayList<>();
    for(String basePackage : new String[] { "biz.softfor.util", "biz.softfor.util.api" }) {
      log.info(basePackage);
      classes.addAll(Reflection.getClasses(basePackage));
    }
    for(Class c : classes) {
      System.out.println(c.getName());
    }
  }

  @Test
  public void longHashTest() {
    log.info("MathUtil.longHash(\"null\")=" + StringUtil.longHash(StringUtil.NULL));
    for(Class c : Reflection.getClasses("javax.servlet")) {
      log.info(c.getName());
    }
  }

  @Test
  public void parentClassFromField() throws Exception {
    Field f = ServerError.class.getField("code");
    System.out.println(f.getDeclaringClass());
    System.out.println(f.getAnnotatedType());
  }

  @Test
  public void trace() {
    for(int i = 0; i < 10; ++i) {
      System.out.println(i + ": " + Reflection.trace(i));
    }
  }

}
