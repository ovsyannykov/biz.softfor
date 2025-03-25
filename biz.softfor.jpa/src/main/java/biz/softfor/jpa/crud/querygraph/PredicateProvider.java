package biz.softfor.jpa.crud.querygraph;

import biz.softfor.jpa.JpaUtil;
import biz.softfor.util.api.ClientError;
import biz.softfor.util.api.filter.FilterId;
import jakarta.persistence.criteria.CommonAbstractCriteria;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.text.MessageFormat;
import java.util.function.BiFunction;
import org.apache.commons.lang3.function.TriFunction;

public final class PredicateProvider {

  public final static String DELETE = "delete";
  public final static String UPDATE = "update";
  public final static String The_filter_must_be_not_empty
  = "The filter for {1} must be not empty.";

  private final String action;
  private final CriteriaBuilder cb;
  public final Class<?> clazz;
  private final Class<?> idClass;
  private final FilterId<?> filter;
  private BiFunction<PredicateProvider, From<?, ?>, Path> queryRelationFun;
  private TriFunction<PredicateProvider, Root<?>, From<?, ?>, Predicate> filterFun;
  private Root<?> subqueryRoot;
  private BiFunction<PredicateProvider, Subquery<?>, From<?, ?>> subqueryFromFun;
  private BiFunction<PredicateProvider, From<?, ?>, Path> subqueryRelationFun;
  private BiFunction<PredicateProvider, CommonAbstractCriteria, Subquery<?>> subqueryFun;

  public PredicateProvider(
    String action
  , CriteriaBuilder cb
  , Class<?> idClass
  , Class<?> clazz
  , FilterId<?> filter
  ) {
    this(
      action
    , cb
    , idClass
    , clazz
    , filter
    , null
    );
    filterFun = (pvdr, root, join) -> {
      Predicate result = ColumnDescr.where(filter, null, root, cb);
      if(result == null) {
        throw new ClientError
        (MessageFormat.format(The_filter_must_be_not_empty, action));
      }
      return result;
    };
    subqueryFromFun = (pvdr, sq) -> pvdr.subqueryRoot = sq.from(pvdr.clazz);
    subqueryRelationFun = null;
    subqueryFun = (pvdr, cq) -> {
      Subquery<?> sq = cq.subquery(pvdr.idClass);
      From<?, ?> sqFrom = pvdr.subqueryFromFun.apply(pvdr, sq);
      sq.select(pvdr.subqueryRelationFun.apply(pvdr, sqFrom));
      return sq.where(pvdr.filterFun.apply(pvdr, pvdr.subqueryRoot, sqFrom));
    };
  }

  public PredicateProvider(PredicateProvider pvdr) {
    this(
      pvdr.action
    , pvdr.cb
    , pvdr.idClass
    , pvdr.clazz
    , pvdr.filter
    , pvdr.queryRelationFun
    );
    filterFun = pvdr.filterFun;
    subqueryFromFun = pvdr.subqueryFromFun;
    subqueryRelationFun = pvdr.subqueryRelationFun;
    subqueryFun = pvdr.subqueryFun;
  }

  private PredicateProvider(
    String action
  , CriteriaBuilder cb
  , Class<?> idClass
  , Class<?> clazz
  , FilterId<?> filter
  , BiFunction<PredicateProvider, From<?, ?>, Path> queryRelationFun
  ) {
    this.action = action;
    this.cb = cb;
    this.idClass = idClass;
    this.clazz = clazz;
    this.filter = filter;
    this.queryRelationFun = queryRelationFun;
  }

  public void andFilterFun
  (TriFunction<PredicateProvider, Root<?>, From<?, ?>, Predicate> nextFilterFun) {
    TriFunction<PredicateProvider, Root<?>, From<?, ?>, Predicate> ff = filterFun;
    filterFun = (p, root, join) -> cb.and
    (ff.apply(p, root, join), nextFilterFun.apply(p, root, join));
  }

  public Predicate predicate(CommonAbstractCriteria cq, Root<?> cqRoot) {
    Predicate result;
    if(queryRelationFun == null) {
      result = filterFun.apply(this, cqRoot, cqRoot);
    } else {
      Subquery<?> sq = subqueryFun.apply(this, cq);
      result = queryRelationFun.apply(this, cqRoot).in(sq);
    }
    return result;
  }

  public void setRelation(String queryRelation, String relation) {
    queryRelationFun = (p, from) -> from.get(queryRelation);
    subqueryRelationFun = (p, from) -> from.get(relation);
  }

  public void setRelation(String queryRelation, String relation, String join) {
    BiFunction<PredicateProvider, Subquery<?>, From<?, ?>> jf = subqueryFromFun;
    subqueryFromFun = ((p, sq) -> JpaUtil.joinTo(jf.apply(p, sq), join));
    setRelation(queryRelation, relation);
  }

  public BiFunction<PredicateProvider, CommonAbstractCriteria, Subquery<?>>
  getSubqueryFun() {
    return subqueryFun;
  }

  public void setSubqueryFun
  (BiFunction<PredicateProvider, CommonAbstractCriteria, Subquery<?>> subqueryFun) {
    this.subqueryFun = subqueryFun;
  }

  @Override
  public String toString() {
    return action + " " + clazz.getName();
  }

  public void where(CriteriaQuery cq, Root<?> cqRoot) {
    cq.where(predicate(cq, cqRoot));
  }

  public void where(CriteriaDelete cq) {
    cq.where(predicate(cq, cq.getRoot()));
  }

  public void where(CriteriaUpdate cq) {
    cq.where(predicate(cq, cq.getRoot()));
  }

}
