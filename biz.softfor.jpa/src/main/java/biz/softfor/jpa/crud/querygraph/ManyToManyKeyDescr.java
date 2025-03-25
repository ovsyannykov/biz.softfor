package biz.softfor.jpa.crud.querygraph;

import biz.softfor.jpa.JpaUtil;
import biz.softfor.jpa.filter.FilterUtil;
import biz.softfor.util.Reflection;
import biz.softfor.util.api.Identifiable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.CollectionUtils;

class ManyToManyKeyDescr extends ColumnDescr implements RelationKeyDescr {

  private final ManyToManyInf m2mInf;
  private final Class linkClass;

  static Class genericParameter(Field field) {
    if(!Set.class.isAssignableFrom(field.getType())) {
      throw new IllegalStateException("The class of '"
      + field.getDeclaringClass().getCanonicalName() + "#" + field.getName()
      + "' field must be '" + Set.class.getCanonicalName() + "'.");
    }
    return Reflection.genericParameter(field);
  }

  protected ManyToManyKeyDescr(Class<?> parent, Field field)
  throws ClassNotFoundException {
    super(
      parent
    , field
    , toManyKeyName(field.getName())
    , Reflection.idClass(genericParameter(field))
    );
    m2mInf = new ManyToManyInf(field);
    linkClass = m2mInf.linkClass();
  }

  @Override
  public void create(Object value, Identifiable<?> data, EntityManager em)
  throws IllegalAccessException, InstantiationException
  , InvocationTargetException, NoSuchMethodException {
    Set<? extends Number> newIds = (Set<? extends Number>)value;
    if(!newIds.isEmpty()) {
      Number id = (Number)data.getId();
      for(Number newId : newIds) {
        persist(id, newId, em);
      }
    }
  }

  @Override
  public int delete(
    Class<Identifiable<? extends Number>> entityClass
  , EntityManager em
  , CriteriaBuilder cb
  , PredicateProvider pvdr
  ) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    CriteriaDelete crd = cb.createCriteriaDelete(linkClass);
    PredicateProvider nextPvdr = nextPvdr(pvdr);
    nextPvdr.where(crd);
    return em.createQuery(crd).executeUpdate();
  }

  @Override
  public int update(Object value, UpdateCtx ctx) throws IllegalAccessException
  , InstantiationException, InvocationTargetException, NoSuchMethodException {
    int total = 0;
    Set<? extends Number> newIds = (Set<? extends Number>)value;
    if(ctx.isUpdateToNull || newIds != null && newIds.isEmpty()) {
      CriteriaDelete crd = ctx.cb.createCriteriaDelete(linkClass);
      PredicateProvider nextPvdr = nextPvdr(ctx.pvdr);
      nextPvdr.where(crd);
      total += ctx.em.createQuery(crd).executeUpdate();
//delete from users_roles where userId in(select id from users where FILTER)
    } else if(CollectionUtils.isNotEmpty(newIds)) {
      //select id,m2mLink.inverseJoinColumn
      //from ctx.rootClass left join m2mInf.fieldName where id in(FILTER)
      List<Map.Entry<? extends Number, List<? extends Number>>> csLinks
      = new ArrayList<>();
      CriteriaQuery<Tuple> cs = ctx.cb.createQuery(Tuple.class);
      Root<?> csRoot = cs.from(ctx.rootClass);
      Path<?> joinColumnPath = csRoot.get(Identifiable.ID);
      Join<?, ?> csJoin = csRoot.join(m2mInf.fieldName, JoinType.LEFT);
      cs.select
      (ctx.cb.tuple(joinColumnPath, csJoin.get(m2mInf.inverseJoinColumn)));
      ctx.pvdr.where(cs, csRoot);
      cs.orderBy(ctx.cb.asc(joinColumnPath));
      List<Tuple> csList = ctx.em.createQuery(cs).getResultList();
//select u1_0.id,u2_0.roleId from users u1_0
//  left join users_roles u2_0 on u1_0.id=u2_0.userId
//  where u1_0.id in((select u3_0.id from users u3_0 where FILTER)) order by 1 asc
      List itemLinks = null;
      Number id = null;
      for(Tuple t : csList) {
        Number joinId = (Number)t.get(0);
        if(!joinId.equals(id)) {
          id = joinId;
          itemLinks = new ArrayList();
          csLinks.add(new AbstractMap.SimpleImmutableEntry(id, itemLinks));
        }
        Object inverseJoinId = t.get(1);
        if(inverseJoinId != null) {
          itemLinks.add(inverseJoinId);
        }
      }

      //delete links that are missing in the newIds
      CriteriaDelete crd = ctx.cb.createCriteriaDelete(linkClass);
      Root cdRoot = crd.getRoot();
      Path joinPath = cdRoot.get(m2mInf.joinColumn);
      Path inverseJoinPath = cdRoot.get(m2mInf.inverseJoinColumn);
      Predicate cdWhere = null;
      for(Map.Entry<? extends Number, List<? extends Number>> csItem : csLinks) {
        for(Number joinedId : csItem.getValue()) {
          if(joinedId != null && !newIds.contains(joinedId)) {
            cdWhere = FilterUtil.or(
              cdWhere
            , ctx.cb.and(
                ctx.cb.equal(joinPath, csItem.getKey())
              , ctx.cb.equal(inverseJoinPath, joinedId)
              )
            , ctx.cb
            );
          }
        }
      }
      if(cdWhere != null) {
        crd.where(cdWhere);
        total += ctx.em.createQuery(crd).executeUpdate();
//delete from users_roles where
//  userId=567 and roleId=533 or userId=567 and roleId=534
//  or userId=568 and roleId=533 or userId=568 and roleId=534
//  or userId=569 and roleId=534
      }

      //insert links that are missing in the select resultset
      for(Number newId : newIds) {
        for(Map.Entry<? extends Number, List<? extends Number>> csItem : csLinks) {
          if(!csItem.getValue().contains(newId)) {
            persist(csItem.getKey(), newId, ctx.em);
            ++total;
          }
        }
      }
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
    if(v != null && ((Set<?>)v).isEmpty()) {
      v = null;
    }
    if(vOld != null && ((Set<?>)vOld).isEmpty()) {
      vOld = null;
    }
    super.diff(parentClass, parentField, v, vOld, result);
  }

  @Override
  protected Path<?> getPath(From root) {
    //System.out.println("ManyToManyKeyDescr: root=" + root.getJavaType().getSimpleName() + ", name=" + name + ", inverseJoinColumn=" + m2mInf.inverseJoinColumn);
    return JpaUtil.joinTo(root, fieldName).get(Identifiable.ID);
  }

  private PredicateProvider nextPvdr(PredicateProvider pvdr) {
    PredicateProvider result = new PredicateProvider(pvdr);
    result.setRelation(m2mInf.joinColumn, Identifiable.ID);
    return result;
  }

  private void persist
  (Number joinColumnId, Number inverseJoinColumnId, EntityManager em)
  throws IllegalAccessException, InvocationTargetException
  , InstantiationException, NoSuchMethodException {
    Object link = linkClass.getConstructor().newInstance();
    PropertyUtils.setProperty(link, m2mInf.joinColumn, joinColumnId);
    PropertyUtils.setProperty(link, m2mInf.inverseJoinColumn, inverseJoinColumnId);
    em.persist(link);
  }

}
