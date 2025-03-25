package biz.softfor.jpa.crud.querygraph;

import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

class ManyToManyColumnDescr extends ToManyColumnDescr<Set> {

  private final ManyToManyInf m2mInf;
  private final Class<?> linkClass;

  protected ManyToManyColumnDescr(Class<?> parent, Field field)
  throws ClassNotFoundException {
    super(parent, field, "_rid", HashSet::new);
    m2mInf = new ManyToManyInf(field);
    linkClass = m2mInf.linkClass();
  }

  @Override
  Predicate where(NodeQueryGraph queryGraph) {
    Root<?> m2mRoot = (Root<?>)queryGraph.cq.getRoots().iterator().next();
    Path<?> joinPath = m2mRoot.get(m2mInf.joinColumn);
    return queryGraph.joinIn(joinPath, joinColumnName);
  }

  @Override
  protected NodeQueryGraph newNode(AbstractQueryGraph queryGraph) {
    CriteriaQuery<Tuple> cq = queryGraph.cb.createQuery(Tuple.class);
    String linkJoinName = queryGraph.from.getJavaType().equals(m2mInf.joinClass)
    ? m2mInf.inverseJoinFieldName : m2mInf.joinFieldName;
    Join<?, ?> linkJoin = cq.from(linkClass).join(linkJoinName);
    return new NodeQueryGraph(queryGraph, this, cq, linkJoin, "");
  }

}
