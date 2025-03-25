package biz.softfor.util;

import java.util.Map;
import java.util.TreeMap;

public class CaseInsensitiveMap<V> extends TreeMap<String, V> {

  public CaseInsensitiveMap() {
    super(String.CASE_INSENSITIVE_ORDER);
  }

  public CaseInsensitiveMap(Map<String, V> src) {
    this();
    if(src != null) {
      putAll(src);
    }
  }

}
