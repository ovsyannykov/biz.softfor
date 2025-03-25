package biz.softfor.jpa.crud.querygraph;

import static biz.softfor.jpa.crud.querygraph.ColumnDescr.copyField;
import biz.softfor.util.api.Identifiable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.beanutils.PropertyUtils;

abstract class ToOneColumnDescr extends RelationColumnDescr {

  protected ToOneColumnDescr(Class<?> parent, Field field) {
    super(parent, field, field.getType());
  }

  @Override
  protected void copy(
    Object result, Object source, String[] fieldParts, int deep
  ) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
    String field = fieldParts[deep];
    Object srcFieldValue = PropertyUtils.getProperty(source, field);
    Object resultFieldValue = PropertyUtils.getProperty(result, field);
    if(srcFieldValue != null) {
      Object resultNode = copy(resultFieldValue, (Identifiable<?>)srcFieldValue);
      copyField(resultNode, srcFieldValue, fieldParts, deep + 1);
      PropertyUtils.setProperty(result, field, resultNode);
    }
  }

}
