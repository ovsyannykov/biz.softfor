package biz.softfor.jpa.crud.querygraph;

import biz.softfor.util.api.Identifiable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import java.lang.reflect.InvocationTargetException;

interface RelationKeyDescr {

  public default void create
  (Object value, Identifiable<?> data, EntityManager em)
  throws IllegalAccessException, InstantiationException
  , InvocationTargetException, NoSuchMethodException {
  }

  public default int delete(
    Class<Identifiable<? extends Number>> entityClass
  , EntityManager em
  , CriteriaBuilder cb
  , PredicateProvider pvdr
  ) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    return 0;
  }

  public default int update(Object value, UpdateCtx ctx) throws IllegalAccessException
  , InstantiationException, InvocationTargetException, NoSuchMethodException {
    return 0;
  }

}
