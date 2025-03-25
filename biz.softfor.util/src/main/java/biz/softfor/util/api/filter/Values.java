package biz.softfor.util.api.filter;

import java.util.ArrayList;

public class Values extends ArrayList<Value> {

  public void add(Object val, Type type) {
    add(new Value(val, type));
  }

  public Object[] getParameters() {
    Object[] result = new Object[size()];
    int i = 0;
    for(Value p : this) {
      result[i++] = p.val;
    }
    return result;
  }

  public int[] getTypes() {
    int[] result = new int[size()];
    int i = 0;
    for(Value p : this) {
      result[i++] = p.type.getJdbcType();
    }
    return result;
  }

}
