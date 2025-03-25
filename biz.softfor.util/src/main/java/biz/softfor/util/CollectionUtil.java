package biz.softfor.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CollectionUtil {

  public static <T> List<T> add(List<T> l, T... a) {
    if(l == null) {
      l = new ArrayList<>(a.length);
    }
    l.addAll(Arrays.asList(a));
    return l;
  }

}
