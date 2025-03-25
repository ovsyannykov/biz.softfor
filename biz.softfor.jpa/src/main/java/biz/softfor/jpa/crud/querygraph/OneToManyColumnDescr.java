package biz.softfor.jpa.crud.querygraph;

import biz.softfor.jpa.JpaUtil;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

class OneToManyColumnDescr extends ToManyColumnDescr<List> {

  private final OneToManyInf o2mInf;

  protected OneToManyColumnDescr(Class<?> parent, Field field)
  throws ClassNotFoundException {
    super(parent, field, JpaUtil.oneToManyJoinColumnName(field), ArrayList::new);
    o2mInf = new OneToManyInf(field);
  }

  @Override
  Predicate where(NodeQueryGraph queryGraph) {
    Path<?> joinPath = o2mInf.joinCd.getPath(queryGraph.from);
    return queryGraph.joinIn(joinPath, joinColumnName);
  }

  @Override
  protected NodeQueryGraph newNode(AbstractQueryGraph queryGraph) {
    CriteriaQuery<Tuple> cq = queryGraph.cb.createQuery(Tuple.class);
    return new NodeQueryGraph(queryGraph, this, cq, cq.from(clazz), "");
  }

}
