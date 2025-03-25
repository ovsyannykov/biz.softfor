package biz.softfor.jpa.filter;

import biz.softfor.jpa.JpaUtil;
import biz.softfor.jpa.crud.querygraph.ColumnDescr;
import biz.softfor.util.DateUtil;
import biz.softfor.util.Json;
import biz.softfor.util.MapUtil;
import biz.softfor.util.StringUtil;
import biz.softfor.util.api.ClientError;
import biz.softfor.util.api.filter.Expr;
import biz.softfor.util.api.filter.Type;
import biz.softfor.util.api.filter.Value;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import org.apache.commons.lang3.function.TriFunction;

public class Stmt {

  public final From<?, ?> from;
  public final CriteriaBuilder cb;

  static ObjectMapper objectMapper;
  private static DateUtil.Parser dateParser;

  private final static boolean DEBUG = false;

  public static void init(ObjectMapper objectMapper, DateUtil.Parser dateParser) {
    Stmt.objectMapper = objectMapper;
    Stmt.dateParser = dateParser;
  }

  public static void init(ObjectMapper objectMapper) {
    init(objectMapper, new DateUtil.Parser());
  }

  public static void checkArgsSize(List<?> args, int expectedSize) {
    if(args.size() != expectedSize) {
      throw new ClientError(The_size_of_the_arguments_list_must_be_
      + expectedSize + " but is " + args.size() + ": "
      + Json.serialize(objectMapper, args));
    }
  }

  private final static String The_expression_element_is_of_an_unsupported_type
  = "The expression element is of an unsupported type: ";
  private final static String
  The_expression_element_contains_the_unsupported_operation
  = "The expression element contains the unsupported operation ";
  private final static String The_size_of_the_arguments_list_must_be_
  = "The size of the arguments list must be ";

  public Stmt(From<?, ?> from, CriteriaBuilder cb) {
    this.from = from;
    this.cb = cb;
  }

  public Expression<?> value(Object arg) {
    if(DEBUG) {
      System.out.println("value(arg=" + Json.serialize(objectMapper, arg) + ")");
    }
    Expression<?> result;
    if(arg == null) {
      result = null;
    } else {
      if(arg instanceof Expr argAsExpr) {
        result = expr(argAsExpr.getOp(), argAsExpr.getArgs());
      } else if(arg instanceof Value argAsValue) {
        result = value(argAsValue.val, argAsValue.type.name());
      } else if(arg instanceof String argAsString) {
        From<?, ?> root = from;
        String[] parts = argAsString.split(StringUtil.FIELDS_DELIMITER_REGEX);
        for(int i = 0; i < parts.length - 1; ++i) {
          root = JpaUtil.joinTo(root, parts[i]);
        }
        result = ColumnDescr.getPath(root, parts[parts.length - 1]);
      } else if(arg.getClass().isArray()) {
        result = expr(Expr.AND, Arrays.asList((Object[])arg));
      } else if(arg instanceof List argAsList) {
        result = expr(Expr.AND, argAsList);
      } else if(arg instanceof Map argAsMap) {
        if(MapUtil.containsKey(argAsMap, Expr.OP)) {
          result = expr
          ((String)argAsMap.get(Expr.OP), (List<?>)argAsMap.get(Expr.ARGS));
        } else if(MapUtil.containsKey(argAsMap, Value.VAL)) {
          result = value
          (argAsMap.get(Value.VAL), (String)argAsMap.get(Value.TYPE));
        } else {
          throw new ClientError(The_expression_element_is_of_an_unsupported_type
          + Json.serialize(objectMapper, arg));
        }
      } else {
        throw new ClientError(The_expression_element_is_of_an_unsupported_type
        + Json.serialize(objectMapper, arg));
      }
    }
    return result;
  }

  Expression<?> expr(String op, List<?> args) {
    if(DEBUG) {
      System.out.println("expr(op=" + op + ",List args=" + Json.serialize(objectMapper, args) + ")");
    }
    BiFunction<Stmt, List<?>, Expression<?>> stmtExpr = ExprRegistry.data.get(op);
    if(stmtExpr == null) {
      throw new ClientError(
        The_expression_element_contains_the_unsupported_operation + op + ": "
        + Json.serialize(objectMapper, args)
      );
    }
    return stmtExpr.apply(this, args);
  }

  Expression<?> value(Object value, String typeName) {
    if(Type.DATETIME.name().equals(typeName)
    && (value instanceof String valueAsString)) {
      try {
        value = dateParser.parse(valueAsString);
      }
      catch(IOException ex) {
        throw new ClientError(ex);
      }
    }
    return cb.literal(value);
  }

  <T> T[] expressions(List<?> args, Class<T> clazz) {
    if(DEBUG) {
      System.out.println("expressions(List args=" + Json.serialize(objectMapper, args) + ")");
    }
    T[] result = (T[])Array.newInstance(clazz, args.size());
    int i = 0;
    for(Object arg : args) {
      result[i] = (T)value(arg);
      ++i;
    }
    return result;
  }

  Expression<?> function(String op, Class<?> type, List<?> args) {
    return cb.function(op, type, expressions(args, Expression.class));
  }

  <T> Predicate equalPredicate(
    List<?> args
  , TriFunction<CriteriaBuilder, Expression<T>, Expression<T>, Predicate> exprp
  , TriFunction<CriteriaBuilder, Expression<T>, T, Predicate> valp
  ) {
    if(DEBUG) {
      System.out.println("equalPredicate(args=" + Json.serialize(objectMapper, args) + ")");
    }
    checkArgsSize(args, 2);
    Predicate result;
    Expression<T> expr0 = (Expression<T>)value(args.get(0));
    Object arg1 = args.get(1);
    if((arg1 instanceof Value arg1Value)) {
      if(arg1Value.val instanceof Boolean valBoolean) {
        Expression<Boolean> expr = (Expression<Boolean>)expr0;
        result = valBoolean ? cb.isTrue(expr) : cb.isFalse(expr);
      } else {
        result = valp.apply(cb, expr0, (T)value(arg1));
      }
    } else {
      result = exprp.apply(cb, expr0, (Expression<T>)value(args.get(1)));
    }
    return result;
  }

  Predicate inPredicate(List<?> args, boolean isIn) {
    if(DEBUG) {
      System.out.println("in(List args=" + Json.serialize(objectMapper, args) + ", isIn=" + isIn + ")");
    }
    List<Object> values = new ArrayList<>(args.size() - 1);
    int i = 0;
    for(Object arg : args) {
      if(i > 0) {
        if(arg instanceof Collection argCollection) {
          values.addAll(argCollection);
        } else {
          values.add(arg);
        }
      }
      ++i;
    }
    return FilterUtil.inPredicate
    ((Path<?>)value(args.get(0)), values, isIn, cb);
  }

  Predicate likePredicate(
    List<?> args
  , TriFunction
    <CriteriaBuilder, Expression<String>, Expression<String>, Predicate> exprp
  , TriFunction<CriteriaBuilder, Expression<String>, String, Predicate> valp
  ) {
    if(DEBUG) {
      System.out.println("likePredicate(List args=" + Json.serialize(objectMapper, args) + ")");
    }
    checkArgsSize(args, 2);
    Predicate result;
    Expression<String> arg0 = (Expression<String>)value(args.get(0));
    Object arg1 = value(args.get(1));
    if(arg1 instanceof Expression) {
      result = exprp.apply(cb, arg0, (Expression<String>)arg1);
    } else {
      result = valp.apply(cb, arg0, (String)arg1);
    }
    return result;
  }

  Predicate comparePredicate(
    List<?> args
  , TriFunction<
      CriteriaBuilder
    , Expression<? extends Comparable>
    , Expression<? extends Comparable>
    , Predicate
    > exprp
  , TriFunction
    <CriteriaBuilder, Expression<? extends Comparable>, Comparable, Predicate>
    valp
  ) {
    if(DEBUG) {
      System.out.println("comparePredicate(List args=" + Json.serialize(objectMapper, args) + ")");
    }
    Predicate result;
    checkArgsSize(args, 2);
    Expression<? extends Comparable> arg0
    = (Expression<? extends Comparable>)value(args.get(0));
    Object arg1 = value(args.get(1));
    if(arg1 instanceof Expression) {
      result = exprp.apply(cb, arg0, (Expression<? extends Comparable>)arg1);
    } else {
      result = valp.apply(cb, arg0, (Comparable)arg1);
    }
    return result;
  }

}
