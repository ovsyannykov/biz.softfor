package biz.softfor.jpa.filter;

import static biz.softfor.jpa.filter.Stmt.checkArgsSize;
import biz.softfor.util.api.filter.Expr;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class ExprRegistry {

  public final static boolean DEBUG = false;

  public final static Map<String, BiFunction<Stmt, List<?>, Expression<?>>> data
  = Map.ofEntries(
    Map.entry(Expr.AND, (stmt, args) -> stmt.cb.and(stmt.expressions(args, Predicate.class)))
  , Map.entry(Expr.OR, (stmt, args) -> stmt.cb.or(stmt.expressions(args, Predicate.class)))
  , Map.entry(Expr.NOT, (stmt, args) -> ((Predicate)stmt.value(args.get(0))).not())
  , Map.entry(Expr.CONCAT, (stmt, args) -> {
    checkArgsSize(args, 2);
    Expression<String> result;
    Object arg0 = stmt.value(args.get(0));
    Object arg1 = stmt.value(args.get(1));
    if(arg0 instanceof Expression) {
      if(arg1 instanceof Expression) {
        result = stmt.cb.concat((Expression<String>)arg0, (Expression<String>)arg1);
      } else {
        result = stmt.cb.concat((Expression<String>)arg0, (String)arg1);
      }
    } else {
      result = stmt.cb.concat((String)arg0, (Expression<String>)arg1);
    }
    return result;
  })
  , Map.entry(Expr.CONCAT_WS, (stmt, args) -> stmt.function(Expr.CONCAT_WS, String.class, args))
  , Map.entry(Expr.EQUAL, (stmt, args) -> stmt.equalPredicate(args, CriteriaBuilder::equal, CriteriaBuilder::equal))
  , Map.entry(Expr.NOT_EQUAL, (stmt, args) -> stmt.equalPredicate(args, CriteriaBuilder::notEqual, CriteriaBuilder::notEqual))
  , Map.entry(Expr.IS_NULL, (stmt, args) -> stmt.cb.isNull(stmt.value(args.get(0))))
  , Map.entry(Expr.IS_NOT_NULL, (stmt, args) -> stmt.cb.isNotNull(stmt.value(args.get(0))))
  , Map.entry(Expr.GT, (stmt, args) -> stmt.comparePredicate(args, CriteriaBuilder::greaterThan, CriteriaBuilder::greaterThan))
  , Map.entry(Expr.GE, (stmt, args) -> stmt.comparePredicate(args, CriteriaBuilder::greaterThanOrEqualTo, CriteriaBuilder::greaterThanOrEqualTo))
  , Map.entry(Expr.LT, (stmt, args) -> stmt.comparePredicate(args, CriteriaBuilder::lessThan, CriteriaBuilder::lessThan))
  , Map.entry(Expr.LE, (stmt, args) -> stmt.comparePredicate(args, CriteriaBuilder::lessThanOrEqualTo, CriteriaBuilder::lessThanOrEqualTo))
  , Map.entry(Expr.IN, (stmt, args) -> stmt.inPredicate(args, true))
  , Map.entry(Expr.NOT_IN, (stmt, args) -> stmt.inPredicate(args, false))
  , Map.entry(Expr.NOW, (stmt, args) -> stmt.function(Expr.NOW, Date.class, args))
  , Map.entry(Expr.LIKE, (stmt, args) -> stmt.likePredicate(args, CriteriaBuilder::like, CriteriaBuilder::like))
  , Map.entry(Expr.NOT_LIKE, (stmt, args) -> stmt.likePredicate(args, CriteriaBuilder::notLike, CriteriaBuilder::notLike))
  , Map.entry(Expr.SUBSTRING, (stmt, args) -> stmt.cb.substring((Expression<String>)stmt.value(args.get(0)), (Integer)args.get(1), (Integer)args.get(2)))
  , Map.entry(Expr.LOWER, (stmt, args) -> stmt.cb.lower((Expression<String>)stmt.value(args.get(0))))
  , Map.entry(Expr.UPPER, (stmt, args) -> stmt.cb.upper((Expression<String>)stmt.value(args.get(0))))
  );

}
