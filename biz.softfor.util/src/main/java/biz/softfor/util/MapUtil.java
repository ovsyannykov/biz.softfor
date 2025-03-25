package biz.softfor.util;

import java.util.Map;

public class MapUtil {

  public static boolean containsKey(Map map, String key) {
    return map != null && map.containsKey(key);
  }

  public static boolean containsKey(Object map, String key) {
    return map instanceof Map mapAsMap && mapAsMap.containsKey(key);
  }

}
