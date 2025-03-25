package biz.softfor.jpa;

import biz.softfor.util.Reflection;
import biz.softfor.util.StringUtil;
import biz.softfor.util.api.Identifiable;
import jakarta.persistence.Tuple;
import jakarta.persistence.TupleElement;
import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.apache.commons.beanutils.PropertyUtils;

public class TupleUtil {

  public static List<String> cachedProperties(Tuple tuple, Class clazz)
  throws IntrospectionException {
    ArrayList<String> result = new ArrayList<>();
    Set<String> propertyNames = Reflection.getPropertyNames(clazz);
    for(TupleElement e : tuple.getElements()) {
      String alias = e.getAlias();
      if(propertyNames.contains(alias)) {
        result.add(alias);
      }
    }
    return result;
  }

  public static List<String> cachedProperties(
    Tuple tuple
  , Class clazz
  , String prefix
  ) throws IntrospectionException {
    ArrayList<String> result = new ArrayList<>();
    Set<String> propertyNames = Reflection.getPropertyNames(clazz);
    prefix = StringUtil.withPrefix(prefix, "");
    int prefixLen = prefix.length();
    for(TupleElement e : tuple.getElements()) {
      String alias = e.getAlias();
      if(alias.startsWith(prefix)) {
        String property = alias.substring(prefixLen);
        if(propertyNames.contains(property)) {
          result.add(property);
        }
      }
    }
    return result;
  }

  public static <T> T toObject(Tuple tuple, Class<T> clazz)
  throws IntrospectionException, ReflectiveOperationException {
    T result = clazz.getConstructor().newInstance();
    Set<String> propertyNames = Reflection.getPropertyNames(clazz);
    for(TupleElement e : tuple.getElements()) {
      String alias = e.getAlias();
      if(propertyNames.contains(alias)) {
        PropertyUtils.setProperty(result, alias, tuple.get(alias));
      }
    }
    return result;
  }

  public static <T> T toObject(Tuple tuple, Class<T> clazz, String prefix)
  throws IntrospectionException, ReflectiveOperationException {
    T result = clazz.getConstructor().newInstance();
    Set<String> propertyNames = Reflection.getPropertyNames(clazz);
    int prefixLen = prefix.length();
    for(TupleElement e : tuple.getElements()) {
      String alias = e.getAlias();
      if(alias.startsWith(prefix)) {
        String property = alias.substring(prefixLen);
        if(propertyNames.contains(property)) {
          PropertyUtils.setProperty(result, property, tuple.get(alias));
        }
      }
    }
    return result;
  }

  public static <T> T toObject
  (Tuple tuple, Class<T> clazz, List<String> properties)
  throws IntrospectionException, ReflectiveOperationException {
    T result = clazz.getConstructor().newInstance();
    for(String p : properties) {
      PropertyUtils.setProperty(result, p, tuple.get(p));
    }
    return result;
  }

  public static <K extends Number, T extends Identifiable<K>> List<T> toObjects
  (Collection<Tuple> tuples, Class<T> clazz)
  throws IntrospectionException, ReflectiveOperationException {
    int resultSize = tuples == null ? 0 : tuples.size();
    List<T> result = new ArrayList<>(resultSize);
    if(resultSize > 0) {
      List<String> properties
      = TupleUtil.cachedProperties(tuples.iterator().next(), clazz);
      for(Tuple t : tuples) {
        result.add(toObject(t, clazz, properties));
      }
    }
    return result;
  }

  public static <T> T toObject
  (Tuple tuple, Class<T> clazz, List<String> properties, String prefix)
  throws IntrospectionException, ReflectiveOperationException {
    T result = clazz.getConstructor().newInstance();
    for(String p : properties) {
      PropertyUtils.setProperty
      (result, p, tuple.get(StringUtil.withPrefix(prefix, p)));
    }
    return result;
  }

  public static <T> List<T> toObjects
  (Collection<Tuple> tuples, Class<T> clazz, String prefix)
  throws IntrospectionException, ReflectiveOperationException {
    int resultSize = tuples == null ? 0 : tuples.size();
    List<T> result = new ArrayList<>(resultSize);
    if(resultSize > 0) {
      List<String> properties
      = cachedProperties(tuples.iterator().next(), clazz, prefix);
      for(Tuple t : tuples) {
        result.add(toObject(t, clazz, properties, prefix));
      }
    }
    return result;
  }

}
