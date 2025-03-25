package biz.softfor.jpa.crud.querygraph;

import biz.softfor.jpa.SoftforbizFunctionContributor;
import biz.softfor.jpa.IdEntity;
import biz.softfor.jpa.SortUtil;
import biz.softfor.jpa.TupleUtil;
import biz.softfor.util.StringUtil;
import biz.softfor.util.api.ClientError;
import biz.softfor.util.api.CommonResponse;
import biz.softfor.util.api.Identifiable;
import biz.softfor.util.api.ReadRequest;
import biz.softfor.util.api.filter.FilterId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import java.beans.IntrospectionException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;

public class QueryGraph
<K extends Number, E extends IdEntity<K>, F extends FilterId<K>>
extends AbstractQueryGraph<K, E, F> {

  private final boolean needTotal;
  private boolean hasToMany;

  private final static String TOTAL_ALIAS = "_tot";

  public static
  <K extends Number, E extends Identifiable<K>, F extends FilterId<K>>
  QueryGraph read
  (EntityManager em, ReadRequest<K, F> request, Class<E> entityClass) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    QueryGraph result
    = new QueryGraph(request, cb.createQuery(Tuple.class), entityClass, cb);
    result.read(em, request.getFirstRowIndex(), request.getRowsOnPage());
    return result;
  }

  public QueryGraph(
    ReadRequest<K, F> request
  , CriteriaQuery<Tuple> cq
  , Class<E> rootClass
  , CriteriaBuilder cb
  ) {
    super(cq, cq.from(rootClass), cb, "", new HashSet<>());
    try {
      SortUtil.orderBy(request.sort, cq, from, cb);
    }
    catch(IntrospectionException ex) {
      throw new ClientError(ex);
    }
    where = ColumnDescr.where(request.filter, null, from, cb);
    needTotal = CollectionUtils.isEmpty(request.fields)
    || request.fields.contains(TOTAL_ALIAS);
    if(needTotal) {
      selections.add(cb.function
        (SoftforbizFunctionContributor.COUNT_ALL_OVER_FUNCTION, Long.class)
        .alias(TOTAL_ALIAS)
      );
    }
    hasToMany = false;
    if(CollectionUtils.isEmpty(request.fields)) {
      String[] f = new String[1];
      Collection<ColumnDescr> cds = ColumnDescr.getPlainCds(rootClass);
      for(ColumnDescr cd : cds) {
        f[0] = cd.name;
        add(f, 0);
      }
    } else {
      for(String f : request.fields) {
        add(f.split(StringUtil.FIELDS_DELIMITER_REGEX), 0);
      }
    }
    select();
  }

  private void joins(From<?, ?> from) {
    if(!hasToMany) {
      Collection<ColumnDescr> cds = ColumnDescr.getCds(from.getJavaType());
      for(Join<?, ?> j : from.getJoins()) {
        String joinAttr = j.getAttribute().getName();
        ColumnDescr joinCd = null;
        for(ColumnDescr cd : cds) {
          if(cd.name.equals(joinAttr)) {
            joinCd = cd;
            break;
          }
        }
        if(joinCd instanceof ToManyColumnDescr
        || joinCd instanceof OneToManyKeyDescr
        || joinCd instanceof ManyToManyKeyDescr) {
          hasToMany = true;
          break;
        }
        joins(j);
      }
    }
  }

  public final void read(EntityManager em, int offset, int limit) {
    joins(from);
    tuples = em.createQuery(cq.distinct(hasToMany))
    .setFirstResult(offset).setMaxResults(limit).getResultList();
    if(DEBUG) {
      System.out.println("QueryGraph.read " + tuples.size() + " rows:");
      for(Tuple t : tuples) {
        System.out.println(t.toString());
      }
    }
    if(CollectionUtils.isNotEmpty(tuples)) {
      for(NodeQueryGraph node : nodes.values()) {
        node.cd.read(node, em);
      }
    }
  }

  public final <K extends Number, O extends Identifiable<K>> CommonResponse<O>
  toResponse(Class<O> outputClass)
  throws IntrospectionException, ReflectiveOperationException {
    List<O> data = TupleUtil.toObjects(tuples, outputClass);
    if(!data.isEmpty()) {
      for(NodeQueryGraph node : nodes.values()) {
        node.cd.toResponse(node, data);
      }
    }
    return new CommonResponse<>(data, !needTotal || tuples.isEmpty()
    ? 0L : (Long)tuples.get(0).get(TOTAL_ALIAS));
  }

}
