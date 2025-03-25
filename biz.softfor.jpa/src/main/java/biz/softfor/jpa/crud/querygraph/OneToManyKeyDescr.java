package biz.softfor.jpa.crud.querygraph;

import biz.softfor.jpa.crud.AbstractCrudSvc;
import biz.softfor.util.Reflection;
import biz.softfor.util.api.ClientError;
import biz.softfor.util.api.Identifiable;
import biz.softfor.util.api.ServerError;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;

class OneToManyKeyDescr extends ColumnDescr implements RelationKeyDescr {

  private final OneToManyInf o2mInf;
  private final Class<Identifiable<? extends Number>> worJoinClass;

  protected OneToManyKeyDescr(Class<?> parent, Field field)
  throws ClassNotFoundException {
    super(
      parent
    , field
    , toManyKeyName(field.getName())
    , Reflection.genericParameter(field)
    );
    o2mInf = new OneToManyInf(field);
    worJoinClass = (Class<Identifiable<? extends Number>>)o2mInf.worJoinClass();
  }

  @Override
  public void create(Object value, Identifiable<?> data, EntityManager em)
  throws IllegalAccessException, InstantiationException
  , InvocationTargetException, NoSuchMethodException {
    List<? extends Number> newIds = (List<? extends Number>)value;
    if(!newIds.isEmpty()) {
      CriteriaBuilder cb = em.getCriteriaBuilder();
      CriteriaUpdate cu = cb.createCriteriaUpdate(worJoinClass);
      cu.set(o2mInf.joinColumnName, data.getId());
      cu.where(cu.getRoot().get(Identifiable.ID).in(newIds));
      int total = em.createQuery(cu).executeUpdate();
      if(total != newIds.size()) {
        throw new ServerError(MessageFormat.format(
          AbstractCrudSvc.X_TO_MANY_UPDATE_ERROR
        , newIds.size(), total, o2mInf.joinClass.getName(), newIds.toString())
        );
      }
    }
  }

  @Override
  public int delete(
    Class<Identifiable<? extends Number>> entityClass
  , EntityManager em
  , CriteriaBuilder cb
  , PredicateProvider pvdr
  ) throws IllegalAccessException, InvocationTargetException
  , NoSuchMethodException {
    int result;
    if(o2mInf.orphanRemoval) {
      result = delete(worJoinClass, nextPvdr(pvdr), em, cb);
//delete from person_files where personDetailsId in (
//  select person_files.id from partners left join personDetails left join person_files
//  where partners.id=3201)
//
//delete from contacts where partnerId in(select id from partners where FILTER)
    } else {
      CriteriaUpdate cru = cb.createCriteriaUpdate(worJoinClass);
      cru.set(o2mInf.joinCd.getPath(cru.getRoot()), cb.nullLiteral(Object.class));
      PredicateProvider nextPvdr = new PredicateProvider(pvdr);
      nextPvdr.setRelation(o2mInf.joinColumnName, o2mInf.joinCd.name, fieldName);
      nextPvdr.where(cru);
      result = em.createQuery(cru).executeUpdate();
//update users set personId=null where personId in(
//  select id from partners where FILTER)
    }
    return result;
  }

  @Override
  public int update(Object value, UpdateCtx ctx) throws IllegalAccessException
  , InstantiationException, InvocationTargetException, NoSuchMethodException {
    int total = 0;
    List<? extends Number> newIds = (List<? extends Number>)value;
    if(ctx.response.isEmptyForUpdate() && !ctx.isUpdateToNull
    && !ctx.isResultСounted) {
      CriteriaQuery<Long> cs = ctx.cb.createQuery(Long.class);
      Root<?> csRoot = cs.from(ctx.rootClass);
      cs.select(ctx.cb.count(csRoot));
      ctx.pvdr.where(cs, csRoot);
      ctx.count = ctx.em.createQuery(cs).getSingleResult().intValue();
//select count(p1_0.id) from partners p1_0 where FILTER
      ctx.isResultСounted = true;
    }
    if(ctx.count > 1 && CollectionUtils.isNotEmpty(newIds)) {
      throw new ClientError(MessageFormat.format(
        AbstractCrudSvc.Update_multiple_records_with_OneToMany_items
      , ctx.count, name
      ));
    }
    boolean updateIds = ctx.count == 1 && CollectionUtils.isNotEmpty(newIds);
    if(ctx.isUpdateToNull || ctx.count == 1 || CollectionUtils.isEmpty(newIds)) {
      if(o2mInf.orphanRemoval) {
        PredicateProvider nextPvdr = nextPvdr(ctx.pvdr);
        if(updateIds) {
          nextPvdr.andFilterFun
          ((p, root, join) -> join.get(Identifiable.ID).in(newIds).not());
        }
        total += delete(worJoinClass, nextPvdr, ctx.em, ctx.cb);
//delete from contacts where id in(
//  select contacts.id from partners join contacts
//  where FILTER and contacts.id not in(newIds))
//
//delete from contactDetails where id in(
//  select contactDetails.id from partners join contacts join contactDetails
//  where FILTER and contacts.id not in(newIds))
//
//delete from person_files where id in (
//  select person_files.id from partners join client_details join personDetails join person_files
//  where FILTER)
      } else {
        CriteriaUpdate cru = ctx.cb.createCriteriaUpdate(worJoinClass);
        Path<?> joinColumnPath = cru.getRoot().get(o2mInf.joinColumnName);
        cru.set(joinColumnPath, ctx.cb.nullLiteral(Object.class));
        PredicateProvider nextPvdr = nextPvdrByJoinColumn(ctx.pvdr);
        nextPvdr.where(cru);
        total += ctx.em.createQuery(cru).executeUpdate();
//update users set personId=null where personId in(
//  select id from partners where FILTER)
      }
    }
    if(updateIds) {
      CriteriaUpdate cru = ctx.cb.createCriteriaUpdate(o2mInf.joinClass);
      PredicateProvider nextPvdr = nextPvdrByJoinColumn(ctx.pvdr);
      Root<?> cruRoot = cru.getRoot();
      Subquery<?> sq = nextPvdr.getSubqueryFun().apply(nextPvdr, cru);
      cru.set(o2mInf.joinCd.getPath(cruRoot), sq);
      cru.where(cruRoot.get(Identifiable.ID).in(newIds));
      total += ctx.em.createQuery(cru).executeUpdate();
//update contacts set partnerId=(select id from partners where FILTER)
//  where contacts.id in(newIds)
//
//update users set personId=(select id from partners where FILTER)
//  where users.id in(newIds)
    }
    return total;
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
    if(v != null && ((Collection<?>)v).isEmpty()) {
      v = null;
    }
    if(vOld != null && ((Collection<?>)vOld).isEmpty()) {
      vOld = null;
    }
    super.diff(parentClass, parentField, v, vOld, result);
  }

  private PredicateProvider nextPvdrByJoinColumn(PredicateProvider pvdr) {
    PredicateProvider result = new PredicateProvider(pvdr);
    result.setRelation(o2mInf.joinColumnName, Identifiable.ID);
    return result;
  }

  private PredicateProvider nextPvdr(PredicateProvider pvdr) {
    PredicateProvider result = new PredicateProvider(pvdr);
    result.setRelation(Identifiable.ID, Identifiable.ID, fieldName);
    return result;
  }

}
