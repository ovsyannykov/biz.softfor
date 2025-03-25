package biz.softfor.jpa.crud.querygraph;

import biz.softfor.jpa.JpaUtil;
import biz.softfor.jpa.TupleUtil;
import biz.softfor.jpa.crud.AbstractCrudSvc;
import biz.softfor.util.Reflection;
import biz.softfor.util.StringUtil;
import biz.softfor.util.api.ClientError;
import biz.softfor.util.api.CommonResponse;
import biz.softfor.util.api.CreateRequest;
import biz.softfor.util.api.Identifiable;
import biz.softfor.util.api.ServerError;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import java.beans.IntrospectionException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import org.apache.commons.beanutils.PropertyUtils;

class OneToOneColumnDescr extends ToOneColumnDescr implements RelationKeyDescr {

  private final Class<Identifiable<? extends Number>> classWor;

  protected OneToOneColumnDescr(Class<?> parent, Field field) {
    super(parent, field);
    try {
      classWor = (Class<Identifiable<? extends Number>>)Reflection.worClass(clazz);
    } catch(ClassNotFoundException ex) {
      throw new ServerError(ex);
    }
  }

  @Override
  public void create(Object value, Identifiable<?> data, EntityManager em)
  throws IllegalAccessException, InstantiationException
  , InvocationTargetException, NoSuchMethodException {
    Identifiable v = (Identifiable<?>)value;
    v.setId(data.getId());
    AbstractCrudSvc subSvc = AbstractCrudSvc.service(clazz);
    CommonResponse subResponse = subSvc.create(new CreateRequest(v));
    if(!subResponse.isOk()) {
      throw new ClientError(subResponse.getDescr());
    }
    PropertyUtils.setProperty(data, name, subResponse.getData(0));
  }

  @Override
  public int delete(
    Class<Identifiable<? extends Number>> entityClass
  , EntityManager em
  , CriteriaBuilder cb
  , PredicateProvider pvdr
  ) throws IllegalAccessException, InvocationTargetException
  , NoSuchMethodException {
    return delete(classWor, nextPvdr(pvdr), em, cb);
//delete from partnerDetails where id in(select id from partners where FILTER)
//
//delete from personDetails where id in(select id from partners where FILTER)
//
//delete from contactDetails where id in(
//  select contacts.id from partners join contacts where FILTER)
  }

  @Override
  public int update(Object value, UpdateCtx ctx) throws IllegalAccessException
  , InstantiationException, InvocationTargetException, NoSuchMethodException {
    int total = 0;
    if(value != null || ctx.isUpdateToNullSubfield(name)) {
      ctx.response.isEmptyForUpdate(true);
      ctx.response.isEmptyForUpdateToNotNull(true);
        total = AbstractCrudSvc.service(clazz).updateInternal(
        (Identifiable<?>)value
      , ctx.updateToNull
      , name
      , ctx.cb
      , nextPvdr(ctx.pvdr)
      , ctx.response
      );
      if(!ctx.response.isEmptyForUpdateToNotNull()) {
        CriteriaQuery cq = ctx.cb.createQuery();
        Root root = cq.from(ctx.pvdr.clazz);
        Join join = JpaUtil.joinTo(root, name);
        cq.select(root.get(Identifiable.ID));
        cq.where
        (ctx.cb.isNull(join.get(Identifiable.ID)), ctx.pvdr.predicate(cq, root));
        List<? extends Number> ids = ctx.em.createQuery(cq).getResultList();
        for(Number id : ids) {
          Identifiable newValue
          = classWor.getConstructor(classWor).newInstance(value);
          newValue.setId(id);
          ctx.em.persist(newValue);
          ++total;
        }
      }
    } else if(ctx.isUpdateToNull) {
      total = delete(classWor, nextPvdr(ctx.pvdr), ctx.em, ctx.cb);
    }
    return total;
  }

  @Override
  protected <K extends Number, O extends Identifiable<K>> List<O> dataToResult
  (NodeQueryGraph queryGraph, Collection<O> data)
  throws IntrospectionException, ReflectiveOperationException {
    List<O> result
    = TupleUtil.toObjects(queryGraph.tuples, clazz, queryGraph.prefix);
    for(Identifiable dataItem : data) {
      Object id = dataItem.getId();
      if(id != null) {
        for(Identifiable nodeDataItem : result) {
          if(id.equals(nodeDataItem.getId())) {
            PropertyUtils.setProperty(dataItem, name, nodeDataItem);
          }
        }
      }
    }
    return result;
  }

  @Override
  protected void diff(
    Class<?> parentClass
  , String parentField
  , Object v
  , Object vOld
  , DiffContext result
  ) throws IllegalAccessException, IllegalArgumentException
  , InstantiationException, InvocationTargetException, NoSuchMethodException {
    String fieldPath = StringUtil.field(parentField, name);
      DiffContext vResult = ColumnDescr.diff
      (fieldPath, classWor, (Identifiable)v, (Identifiable)vOld);
      if(vResult.changed) {
        result.changed = true;
        if(vResult.data == null) {
          if(vResult.all2Null) {
            result.updateToNull(fieldPath);
          } else {
            result.updateToNull(vResult);
          }
        } else {
          result.setProperty(parentClass, name, vResult.data);
          result.updateToNull(vResult);
        }
      } else {
        result.all2Null = false;
      }
  }

  private PredicateProvider nextPvdr(PredicateProvider pvdr) {
    PredicateProvider result = new PredicateProvider(pvdr);
    result.setRelation(Identifiable.ID, Identifiable.ID, fieldName);
    return result;
  }

}
