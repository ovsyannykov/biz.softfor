package biz.softfor.jpa.crud.querygraph;

import biz.softfor.jpa.TupleUtil;
import biz.softfor.util.StringUtil;
import biz.softfor.util.api.Identifiable;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Path;
import java.beans.IntrospectionException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.beanutils.PropertyUtils;

class ManyToOneColumnDescr extends ToOneColumnDescr {

  protected ManyToOneColumnDescr(Class<?> parent, Field field) {
    super(parent, field);
  }

  @Override
  protected <K extends Number, O extends Identifiable<K>> List<O> dataToResult
  (NodeQueryGraph queryGraph, Collection<O> data)
  throws IntrospectionException, ReflectiveOperationException {
    List<O> result = new ArrayList<>();
    List<String> properties = TupleUtil.cachedProperties
    ((Tuple)queryGraph.tuples.iterator().next(), clazz, queryGraph.prefix);
    for(Tuple t : (List<Tuple>)queryGraph.tuples) {
      String wpfx = StringUtil.withPrefix(queryGraph.prefix, Identifiable.ID);
      Object id = t.get(wpfx);
      if(id != null) {
        O newItem = null;
        for(O nodeDataItem : result) {
          if(id.equals(nodeDataItem.getId())) {
            newItem = nodeDataItem;
            break;
          }
        }
        if(newItem == null) {
          newItem = (O)TupleUtil.toObject
          (t, clazz, properties, queryGraph.prefix);
          result.add(newItem);
        }
        wpfx = StringUtil.withPrefix(queryGraph.parent.prefix, Identifiable.ID);
        Object relationId = t.get(wpfx);
        for(Identifiable dataItem : data) {
          if(dataItem.getId().equals(relationId)) {
            PropertyUtils.setProperty(dataItem, name, newItem);
          }
        }
      }
    }
    return result;
  }

  @Override
  protected Path<?> getPath(From root) {
    return root.get(name).get(Identifiable.ID);
  }

}
