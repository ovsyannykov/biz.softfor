package biz.softfor.util.api.filter;

import java.util.Date;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class Value {

  public final Object val;
  public final static String VAL = "val";

  public final Type type;
  public final static String TYPE = "type";

  public Value(Object val, Type type) {
    this.val = val;
    this.type = type;
  }

  public Value(Object val) {
    this(val, val instanceof Date ? Type.DATETIME : Type.VARCHAR);
  }

  public Value() {
    this(null);
  }

}
