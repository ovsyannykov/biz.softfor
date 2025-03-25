package biz.softfor.jpa.crud.querygraph;

import biz.softfor.jpa.TupleUtil;
import biz.softfor.util.Reflection;
import biz.softfor.util.api.Identifiable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.Selection;
import java.beans.IntrospectionException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import org.apache.commons.beanutils.PropertyUtils;

class ToManyColumnDescr<COL extends Collection> extends RelationColumnDescr {

  protected final String joinColumnName;
  private final Function<Integer, COL> newValues;

  protected ToManyColumnDescr(
    Class<?> parent
  , Field field
  , String joinColumnName
  , Function<Integer, COL> newValues
  ) {
    super(parent, field, Reflection.genericParameter(field));
    this.joinColumnName = joinColumnName;
    this.newValues = newValues;
  }

  @Override
  protected void copy
  (Object result, Object source, String[] fieldParts, int deep)
  throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
    String field = fieldParts[deep];
    COL srcFieldValue = (COL)PropertyUtils.getProperty(source, field);
    if(srcFieldValue != null) {
      COL resultFieldValue = (COL)PropertyUtils.getProperty(result, field);
      if(resultFieldValue == null) {
        resultFieldValue = newValues.apply(srcFieldValue.size());
      }
      for(Object s : srcFieldValue) {
        Identifiable<?> resultItem = null;
        for(Object r : resultFieldValue) {
          if(((Identifiable<?>)r).getId().equals(((Identifiable<?>)s).getId())) {
            resultItem = (Identifiable<?>)r;
            break;
          }
        }
        if(resultItem == null) {
          resultItem = copy(null, (Identifiable<?>)s);
          resultFieldValue.add(resultItem);
        }
        copyField(resultItem, s, fieldParts, deep + 1);
      }
      PropertyUtils.setProperty(result, field, resultFieldValue);
    }
  }

  @Override
  protected <K extends Number, O extends Identifiable<K>> List<O> dataToResult
  (NodeQueryGraph queryGraph, Collection<O> data)
  throws IntrospectionException, ReflectiveOperationException {
    List<O> result = new ArrayList<>();
    List<String> properties = TupleUtil.cachedProperties
    ((Tuple)queryGraph.tuples.iterator().next(), clazz);
    Map<Object, COL> itemsMap = new HashMap<>();
    for(Tuple t : (List<Tuple>)queryGraph.tuples) {
      Object id = t.get(Identifiable.ID);
      O newItem = null;
      for(O nodeDataItem : result) {
        if(id.equals(nodeDataItem.getId())) {
          newItem = nodeDataItem;
          break;
        }
      }
      if(newItem == null) {
        newItem = (O)TupleUtil.toObject(t, clazz, properties);
        result.add(newItem);
      }
      Object relationId = t.get(joinColumnName);
      COL items = itemsMap.get(relationId);
      if(items == null) {
        items = newValues.apply(2);
        itemsMap.put(relationId, items);
      }
      items.add(newItem);
    }
    for(Identifiable dataItem : data) {
      COL items = itemsMap.get(dataItem.getId());
      if(items != null) {
        PropertyUtils.setProperty(dataItem, name, items);
      }
    }
    return result;
  }

  @Override
  protected Set<Selection> newSelections(AbstractQueryGraph queryGraph) {
    return new HashSet<>();
  }

  @Override
  protected void readData(NodeQueryGraph queryGraph, EntityManager em) {
    queryGraph.join(em);
  }

}
