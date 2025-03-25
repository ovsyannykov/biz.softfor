package biz.softfor.util.api.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class Expr {

  public final static String AND = "AND";
  public final static String OR = "OR";
  public final static String NOT = "NOT";
  public final static String CONCAT = "CONCAT";
  public final static String CONCAT_WS = "CONCAT_WS";
  public final static String IS_NULL = "IS NULL";
  public final static String IS_NOT_NULL = "IS NOT NULL";
  public final static String EQUAL = "=";
  public final static String NOT_EQUAL = "<>";
  public final static String GT = ">";
  public final static String GE = ">=";
  public final static String LT = "<";
  public final static String LE = "<=";
  public final static String IN = "IN";
  public final static String NOT_IN = "NOT IN";
  public final static String LIKE = "LIKE";
  public final static String NOT_LIKE = "NOT LIKE";
  public final static String NOW = "NOW";
  public final static String SUBSTRING = "SUBSTRING";
  public final static String LOWER = "LOWER";
  public final static String UPPER = "UPPER";

  private String op;
  public final static String OP = "op";

  private List<?> args;//Value || Expr || Expr[]
  public final static String ARGS = "args";

  public Expr(String op, List<?> args) {
    setOp(op);
    setArgs(args);
  }

  public Expr(String op, Object... args) {
    this(op, Arrays.asList(args));
  }

  public Expr() {
    this(null, new ArrayList());
  }

  public final void setArgs(List<?> args) {
    this.args = args;
  }

  public final void setOp(String op) {
    this.op = StringUtils.upperCase(op);
  }

}
