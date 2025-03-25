package biz.softfor.testutil;

import biz.softfor.util.Holder;
import biz.softfor.util.StringUtil;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.ArrayUtils;

public class IgnoringFields {

  public final Class<?> clazz;
  public final Set<String> fields;
  public final String[] parentFields;
  public final Set<IgnoringFields> children;

  public IgnoringFields
  (Class<?> clazz, Set<String> fields, String... parentFields) {
    this.clazz = clazz;
    this.fields = fields;
    this.parentFields
    = ArrayUtils.isEmpty(parentFields) ? new String[] { "" } : parentFields;
    children = new HashSet<>();
  }

  public IgnoringFields(Class<?> clazz) {
    this(clazz, Set.of());
  }

  public String[] names(String... fields) {
    int size = fields.length + this.fields.size() * parentFields.length;
    for(IgnoringFields c : children) {
      size += c.fields.size() * c.parentFields.length;
    }
    String[] result = new String[size];
    Holder<Integer> i = new Holder<>(0);
    for(String f : fields) {
      result[i.value] = fields[i.value];
      ++i.value;
    }
    names(result, i);
    for(IgnoringFields c : children) {
      c.names(result, i);
    }
    return result;
  }

  public FilterProvider jsonFilter() {
    FilterProvider result
    = filterProvider(new SimpleFilterProvider().setFailOnUnknownId(false));
    for(IgnoringFields c : children) {
      result = c.filterProvider(result);
    }
    return result;
  }

  private void names(String[] result, Holder<Integer> i) {
    for(String p : parentFields) {
      for(String f : fields) {
        result[i.value] = p.isEmpty() ? f : (p + StringUtil.FIELDS_DELIMITER + f);
        ++i.value;
      }
    }
  }

  private FilterProvider filterProvider(FilterProvider result) {
    String filterName = clazz.getAnnotation(JsonFilter.class).value();
    SimpleBeanPropertyFilter pf
    = SimpleBeanPropertyFilter.serializeAllExcept(fields);
    return ((SimpleFilterProvider)result).addFilter(filterName, pf);
  }

}
