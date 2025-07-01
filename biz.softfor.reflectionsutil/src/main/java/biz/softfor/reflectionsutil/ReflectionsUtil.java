package biz.softfor.reflectionsutil;

import biz.softfor.util.Constants;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ArrayUtils;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

public class ReflectionsUtil {

  public static void scan(
    String dir
  , Class<? extends Annotation> anno
  , String[] packages
  , String[] excludePackages
  ) throws IOException {
    FilterBuilder fb = new FilterBuilder();
    for(String p : packages) {
      fb.includePackage(p);
    }
    if(ArrayUtils.isNotEmpty(excludePackages)) {
      for(String p : excludePackages) {
        fb.excludePackage(p);
      }
    }
    ConfigurationBuilder cb = new ConfigurationBuilder().forPackages(packages)
    .filterInputsBy(fb).setScanners(Scanners.TypesAnnotated);
    Reflections reflections = new Reflections(cb);
    Set<Class<?>> types
    = reflections.getTypesAnnotatedWith((Class<? extends Annotation>)anno);
    if(!types.isEmpty()) {
      File dirFile = new File(dir);
      if(!dirFile.exists()) {
        dirFile.mkdir();
      }
      String filename
      = dir + File.separator + anno.getSimpleName() + Constants.REFLECTIONS_EXT;
      try(FileWriter fileWriter = new FileWriter(filename);
      BufferedWriter writer = new BufferedWriter(fileWriter)) {
        for(String n : types.stream().map(Class::getName).sorted().collect(Collectors.toList())) {
          writer.write(n + "\n");
        }
      }
    }
  }

}
