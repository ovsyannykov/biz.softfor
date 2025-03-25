package biz.softfor.jpa.crud.querygraph;

import biz.softfor.util.api.Identifiable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Selection;
import java.beans.IntrospectionException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;

abstract class RelationColumnDescr extends ColumnDescr {

  protected RelationColumnDescr
  (Class<?> parent, Field field, String name, Class<?> clazz) {
    super(parent, field, name, clazz);
  }

  protected RelationColumnDescr(Class<?> parent, Field field, Class clazz) {
    super(parent, field, clazz);
  }

  final void read(NodeQueryGraph queryGraph, EntityManager em) {
    readData(queryGraph, em);
    if(CollectionUtils.isNotEmpty(queryGraph.tuples)) {
      Collection<NodeQueryGraph> nodes = queryGraph.nodes.values();
      for(NodeQueryGraph node : nodes) {
        node.cd.read(node, em);
      }
    }
  }

  final <K extends Number, O extends Identifiable<K>> void toResponse
  (NodeQueryGraph queryGraph, Collection<O> data)
  throws IntrospectionException, ReflectiveOperationException {
    if(CollectionUtils.isNotEmpty(queryGraph.tuples)) {
      List<O> nodeData = dataToResult(queryGraph, data);
      Collection<NodeQueryGraph> nodes = queryGraph.nodes.values();
      for(NodeQueryGraph node : nodes) {
        node.cd.toResponse(node, nodeData);
      }
    }
  }

  Predicate where(NodeQueryGraph queryGraph) {
    return null;
  }

  @Override
  protected void addTo
  (AbstractQueryGraph queryGraph, String[] parts, int partIdx) {
    NodeQueryGraph node = (NodeQueryGraph)queryGraph.nodes.get(name);
    if(node == null) {
      node = newNode(queryGraph);
      queryGraph.nodes.put(name, node);
    }
    ++partIdx;
    if(partIdx < parts.length) {
      node.add(parts, partIdx);
    } else {
      for(ColumnDescr cd : getPlainCds(clazz)) {
        cd.addTo(node);
      }
    }
  }

  protected abstract <K extends Number, O extends Identifiable<K>>
  List<O> dataToResult(NodeQueryGraph queryGraph, Collection<O> data)
  throws IntrospectionException, ReflectiveOperationException;

  protected NodeQueryGraph newNode(AbstractQueryGraph queryGraph) {
    return new NodeQueryGraph(queryGraph, this);
  }

  protected Set<Selection> newSelections(AbstractQueryGraph queryGraph) {
    return queryGraph.selections;
  }

  protected void readData(NodeQueryGraph queryGraph, EntityManager em) {
    queryGraph.tuples = queryGraph.parent.tuples;
  }

}
