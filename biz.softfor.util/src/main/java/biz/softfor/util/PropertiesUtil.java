package biz.softfor.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesUtil {

  public static String get(String file, String key, String defaultValue)
  throws IOException {
    Properties ps = new Properties();
    ps.load(new FileInputStream(file));
    return ps.getProperty(key, defaultValue);
  }

}
