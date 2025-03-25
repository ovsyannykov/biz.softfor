package biz.softfor.util;

import biz.softfor.util.api.ServerError;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DynaClassLoader extends ClassLoader {

  @Override
  public Class<?> findClass(String className) {
    String fileName = "target/classes/" + className.replace(".", File.separator);
    try {
      byte[] fileCtnt = Files.newInputStream(Path.of(fileName)).readAllBytes();
      return defineClass(className, fileCtnt, 0, fileCtnt.length);
    }
    catch(IOException ex) {
      throw new ServerError(ex);
    }
  }

}
