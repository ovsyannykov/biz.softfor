package biz.softfor.jpa.crud.querygraph;

import biz.softfor.util.Reflection;
import biz.softfor.util.api.Identifiable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaUpdate;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class ManyToOneWorDescr extends ColumnDescr implements RelationKeyDescr {

  protected ManyToOneWorDescr(Class<?> parent, Field field) {
    super
    (parent, field, manyToOneKeyName(field), Reflection.idClass(field.getType()));
  }

  protected ManyToOneWorDescr(ManyToOneWorDescr cd) {
    super(cd);
  }

  @Override
  public int delete(
    Class<Identifiable<? extends Number>> entityClass
  , EntityManager em
  , CriteriaBuilder cb
  , PredicateProvider pvdr
  ) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    int result = 0;
    String fieldClassWorName = Reflection.worClassName(fieldClass.getName());
    if(fieldClassWorName.equals(entityClass.getName())) {
      CriteriaUpdate cru = cb.createCriteriaUpdate(entityClass);
      cru.set(getPath(cru.getRoot()), cb.nullLiteral(Object.class));
      PredicateProvider nextPvdr = new PredicateProvider(pvdr);
      nextPvdr.setRelation(name, Identifiable.ID);
      nextPvdr.where(cru);
      result = em.createQuery(cru).executeUpdate();
//update partners set parentId=null where parentId in(
//  select id from partners where FILTER)
    }
    return result;
  }

}
