package biz.softfor.jpa.crud.querygraph;

import biz.softfor.jpa.IdEntity;
import biz.softfor.util.StringUtil;
import biz.softfor.util.api.Identifiable;
import biz.softfor.util.api.filter.FilterId;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Selection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

class AbstractQueryGraph
<K extends Number, E extends IdEntity<K>, F extends FilterId<K>> {

  final From<E, E> from;
  final CriteriaBuilder cb;
  final String prefix;
  final Set<Selection> selections;
  final Map<String, NodeQueryGraph> nodes;

  List<Tuple> tuples;

  protected final CriteriaQuery<Tuple> cq;
  protected final Map<String, ColumnDescr> columnDescrs;

  protected Predicate where;

  protected final static boolean DEBUG = false;

  protected AbstractQueryGraph(
    CriteriaQuery<Tuple> cq
  , From<E, E> from
  , CriteriaBuilder cb
  , String prefix
  , Set<Selection> selections
  ) {
    this.cq = cq;
    this.from = from;
    this.cb = cb;
    this.prefix = prefix;
    this.selections = selections;
    nodes = new HashMap<>();
    columnDescrs = ColumnDescr.get(from.getJavaType());
    columnDescrs.get(Identifiable.ID).addTo(this, Identifiable.ID_ARRAY, 0);
  }

  @Override
  public String toString() {
    String sels = "";
    for(Selection<?> selection : selections) {
      if(!sels.isEmpty()) {
        sels += ",";
      }
      sels += selection.getAlias();
    }
    Path parentPath = from.getParentPath();
    String parentPathStr
    = parentPath == null ? StringUtil.NULL : parentPath.getModel().toString();
    String joins = "";
    for(Join j : from.getJoins()) {
      if(!joins.isEmpty()) {
        joins += ",";
      }
      joins += j.getAttribute().getName();
    }
    return "root=" + from.getJavaType().getName()
    + ", parentPath=" + parentPathStr
    + ", joins=" + joins
    + ", selections=" + sels;
  }

  final void add(String[] parts, int partIdx) {
    if(parts.length > 0) {
      ColumnDescr cd = columnDescrs.get(parts[partIdx]);
      if(cd != null) {
        cd.addTo(this, parts, partIdx);
      }
    }
  }

  final void addToSelections(ColumnDescr cd) {
    selections.add
    (from.get(cd.name).alias(StringUtil.withPrefix(prefix, cd.name)));
  }

  final protected void select() {
    if(where != null) {
      cq.where(where);
    }
    cq.select(cb.tuple(selections.toArray(Selection[]::new)));
    for(NodeQueryGraph node : nodes.values()) {
      node.select();
    }
  }

}
