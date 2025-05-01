package biz.softfor.util.api;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface Identifiable<K extends Number> {

  public final static String ID = "id";
  public final static String[] ID_ARRAY = new String[] { Identifiable.ID };

  public abstract K getId();

  public abstract void setId(K id);

  public static <K extends Number> boolean equals(Identifiable<K> l, Object r) {
    boolean result;
    if(l == r) {
      result = true;
    } else if(r == null || l == null) {
      result = false;
    } else if(l.getClass() != r.getClass()) {
      result = false;
    } else {
      Object id = l.getId();
      result = id != null && id.equals(((Identifiable)r).getId());
    }
    return result;
  }

  public static <K extends Number, E extends Identifiable<K>> K id(E v) {
    return v == null ? null : v.getId();
  }

  public static <K extends Number, E extends Identifiable<K>> Set<K> idSet(Iterable<E> items) {
    Set<K> result = null;
    if(items != null) {
      result = new HashSet<>();
      for(E item : items) {
        K id = item.getId();
        if(id != null) {
          result.add(id);
        }
      }
    }
    return result;
  }

  public static <K extends Number, E extends Identifiable<K>> Set<K> idSet(E... items) {
    Set<K> result = null;
    if(items != null) {
      result = new HashSet<>();
      for(E item : items) {
        K id = item.getId();
        if(id != null) {
          result.add(id);
        }
      }
    }
    return result;
  }

  public static <K extends Number, E extends Identifiable<K>> List<K> ids(Iterable<E> items) {
    List<K> result = null;
    if(items != null) {
      result = new ArrayList<>();
      for(E item : items) {
        K id = item.getId();
        if(id != null) {
          result.add(id);
        }
      }
    }
    return result;
  }

  public static <K extends Number, E extends Identifiable<K>> List<K> ids(E... items) {
    List<K> result = null;
    if(items != null) {
      result = new ArrayList<>();
      for(E item : items) {
        K id = item.getId();
        if(id != null) {
          result.add(id);
        }
      }
    }
    return result;
  }

}
