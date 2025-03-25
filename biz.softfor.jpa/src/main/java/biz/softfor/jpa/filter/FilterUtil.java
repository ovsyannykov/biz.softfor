package biz.softfor.jpa.filter;

import biz.softfor.jpa.crud.querygraph.ColumnDescr;
import biz.softfor.util.DateUtil;
import biz.softfor.util.Range;
import biz.softfor.util.api.filter.FilterId;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import org.apache.commons.beanutils.PropertyUtils;

public interface FilterUtil {

  public static Predicate where(
    String propertyName
  , FilterId filter
  , Predicate result
  , From<?, ?> from
  , CriteriaBuilder cb
  ) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    Object v = PropertyUtils.getProperty(filter, propertyName);
    if(v != null) {
      Predicate w;
      if(v instanceof FilterId vFilterId) {
        w = ColumnDescr.where(vFilterId, result, from.join(propertyName), cb);
      } else {
        Path<?> fieldPath = ColumnDescr.getPath(from, propertyName);
        if(v instanceof String vString) {
          w = cb.like(cb.lower((Path<String>)fieldPath), vString.toLowerCase());
        } else if(v instanceof Collection vCollection) {
          w = FilterUtil.inPredicate(fieldPath, vCollection, true, cb);
        } else if(v instanceof Range vRange) {
          w = between(vRange, fieldPath, cb);
        } else {
          w = cb.equal(fieldPath, v);
        }
      }
      result = FilterUtil.and(result, w, cb);
    }
    return result;
  }

  public static Predicate and
  (Predicate result, Predicate predicate, CriteriaBuilder cb) {
    return result == null
    ? predicate : (predicate == null ? result : cb.and(result, predicate));
  }

  public static Predicate or
  (Predicate result, Predicate predicate, CriteriaBuilder cb) {
    return result == null
    ? predicate : (predicate == null ? result : cb.or(result, predicate));
  }

  public static <T extends Comparable<? super T>> Predicate between
  (Range<T> range, Path<?> fieldPath, CriteriaBuilder cb) {
    Predicate w = null;
    Expression<T> p = (Expression<T>)fieldPath;
    T from = range.getFrom();
    if(from != null) {
      w = cb.greaterThanOrEqualTo(p, from);
    }
    T to = range.getTo();
    if(to != null) {
      w = FilterUtil.and(w, cb.lessThan(p, to), cb);
    }
    return w;
  }

  public static Predicate between(
    Path<Date> attrPath
  , Date dateFrom
  , Date dateTo
  , Predicate result
  , Calendar calendar
  , CriteriaBuilder cb
  ) {
    if(dateFrom != null) {
      result = and(result, cb.greaterThanOrEqualTo(attrPath, dateFrom), cb);
    }
    if(dateTo != null) {
      if(DateUtil.isDateOnly(dateTo, calendar)) {
        calendar.setTime(dateTo);
        calendar.add(Calendar.DATE, 1);
        result = and(result, cb.lessThan(attrPath, calendar.getTime()), cb);
      } else {
        result = and(result, cb.lessThanOrEqualTo(attrPath, dateTo), cb);
      }
    }
    return result;
  }

  public static <C> Predicate inPredicate
  (Path<?> attrPath, Collection<C> values, boolean isIn, CriteriaBuilder cb) {
    Predicate result;
    if(values == null || values.isEmpty()) {
      result = cb.disjunction();
    } else if(values.size() == 1) {
      C v = values.iterator().next();
      result = cb.equal(attrPath, v);
    } else {
      result = attrPath.in(values);
    }
    if(!isIn) {
      result = result.not();
    }
    return result;
  }

}
