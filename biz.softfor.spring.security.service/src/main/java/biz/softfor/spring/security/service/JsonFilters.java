package biz.softfor.spring.security.service;

import biz.softfor.jpa.crud.querygraph.ColumnDescr;
import biz.softfor.jpa.crud.querygraph.ManyToManyGeneratedLink;
import biz.softfor.util.StringUtil;
import biz.softfor.util.api.Identifiable;
import biz.softfor.util.api.ReadRequest;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import lombok.extern.java.Log;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Component;

@Component
@Log
public class JsonFilters {

  private final Map<Class<?>, String> jsonFilters;

  public JsonFilters(EntityManager em) {
    jsonFilters = new HashMap<>();
    for(EntityType<?> e : em.getMetamodel().getEntities()) {
      Class<?> t = e.getJavaType();
      JsonFilter jsonFilterAnn = t.getAnnotation(JsonFilter.class);
      if(jsonFilterAnn == null) {
        if(!t.isAnnotationPresent(ManyToManyGeneratedLink.class)) {
          log.severe(() -> "The " + t.getName() + " class is not marked with "
          + "the " + JsonFilter.class.getName() + " annotation.");
        }
      } else {
        jsonFilters.put(t, jsonFilterAnn.value());
      }
    }
  }

  public MappingJacksonValue filter
  (Function<ReadRequest, ?> f, ReadRequest request, Class<?> clazz) {
    MappingJacksonValue result = new MappingJacksonValue(f.apply(request));
    result.setFilters(filter(request.fields, clazz));
    return result;
  };

  public FilterProvider filter(Collection<String> fields, Class<?> clazz) {
    Map<String, Set<String>> allowed = new HashMap<>();
    for(String fieldsElem : fields) {
      Class<?> parentClass = clazz;
      Set<String> allowedFields = filter(allowed, parentClass);
      for(String fieldName : fieldsElem.split(StringUtil.FIELDS_DELIMITER_REGEX)) {
        allowedFields.add(fieldName);
        parentClass = ColumnDescr.get(parentClass).get(fieldName).clazz;
        allowedFields = filter(allowed, parentClass);
      }
    }
    SimpleFilterProvider result = new SimpleFilterProvider();
    for(Map.Entry<String, Set<String>> ai : allowed.entrySet()) {
      PropertyFilter pf;
      Set<String> allowedFields = ai.getValue();
      if(allowedFields.isEmpty()) {
        pf = SimpleBeanPropertyFilter.serializeAll();
      } else {
        allowedFields.add(Identifiable.ID);
        pf = SimpleBeanPropertyFilter.filterOutAllExcept(allowedFields);
      }
      result.addFilter(ai.getKey(), pf);
    }
    return result;
  }

  private Set<String> filter
  (Map<String, Set<String>> allowed, Class<?> parentClass) {
    String filterName = jsonFilters.get(parentClass);
    Set<String> result = allowed.get(filterName);
    if(result == null) {
      result = new HashSet<>();
      allowed.put(filterName, result);
    }
    return result;
  }

}
