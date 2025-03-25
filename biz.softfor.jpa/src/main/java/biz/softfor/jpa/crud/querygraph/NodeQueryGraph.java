package biz.softfor.jpa.crud.querygraph;

import biz.softfor.jpa.IdEntity;
import biz.softfor.jpa.JpaUtil;
import biz.softfor.util.StringUtil;
import biz.softfor.util.api.Identifiable;
import biz.softfor.util.api.filter.FilterId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class NodeQueryGraph
<K extends Number, E extends IdEntity<K>, F extends FilterId<K>>
extends AbstractQueryGraph<K, E, F> {

  final AbstractQueryGraph parent;
  final RelationColumnDescr cd;

  private CriteriaBuilder.In joinIn;

  NodeQueryGraph(
    AbstractQueryGraph parent
  , RelationColumnDescr cd
  , CriteriaQuery<Tuple> cq
  , From<E, E> root
  , String prefix
  ) {
    super(cq, root, parent.cb, prefix, cd.newSelections(parent));
    this.parent = parent;
    this.cd = cd;
    where = cd.where(this);
  }

  NodeQueryGraph(AbstractQueryGraph parent, RelationColumnDescr cd) {
    this(
      parent
    , cd
    , parent.cq
    , (From<E, E>)JpaUtil.joinTo(parent.from, cd.name)
    , StringUtil.withPrefix(parent.prefix, cd.name)
    );
  }

  void join(EntityManager em) {
    Set values = new HashSet();
    for(Tuple rt : (List<Tuple>)parent.tuples) {
      Object id = rt.get(Identifiable.ID);
      if(values.add(id)) {
        joinIn.value(id);
      }
    }
    if(!values.isEmpty()) {
      tuples = em.createQuery(cq).getResultList();
      if(DEBUG) {
        System.out.println("QueryGraph.read " + tuples.size() + " rows:");
        for(Tuple t : tuples) {
          System.out.println(t.toString());
        }
      }
    }
  }

  final Predicate joinIn(Path<?> path, String attribute) {
    selections.add(path.alias(attribute));
    joinIn = cb.in(path);
    return joinIn;
  }

}
