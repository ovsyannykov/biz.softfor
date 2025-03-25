package biz.softfor.jpa.crud.querygraph;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.beanutils.PropertyUtils;

public class DiffContext {

  public Object data = null;
  public List<String> updateToNull = null;
  public boolean all2Null = true;
  public boolean changed = false;

  public void updateToNull(String fieldName) {
    if(updateToNull == null) {
      updateToNull = new ArrayList<>();
    }
    updateToNull.add(fieldName);
  }

  public void updateToNull(DiffContext ctx) {
    if(ctx.updateToNull != null) {
      if(updateToNull == null) {
        updateToNull = new ArrayList<>();
      }
      updateToNull.addAll(ctx.updateToNull);
    }
  }

  public void setProperty(Class<?> parentClass, String name, Object v)
  throws IllegalAccessException, IllegalArgumentException
  , InstantiationException, InvocationTargetException, NoSuchMethodException {
    if(data == null) {
      data = parentClass.getConstructor().newInstance();
    }
    PropertyUtils.setProperty(data, name, v);
    all2Null = false;
  }

}
