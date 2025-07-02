package biz.softfor.spring.jpa.crud;

import biz.softfor.jpa.crud.AbstractCrudSvc;
import biz.softfor.jpa.crud.querygraph.ColumnDescr;
import biz.softfor.jpa.crud.querygraph.ManyToOneWorDescr;
import biz.softfor.jpa.crud.querygraph.PredicateProvider;
import biz.softfor.jpa.crud.querygraph.QueryGraph;
import biz.softfor.jpa.crud.querygraph.UpdateCtx;
import biz.softfor.spring.messagesi18n.I18n;
import biz.softfor.util.StringUtil;
import biz.softfor.util.api.CommonResponse;
import biz.softfor.util.api.CreateRequest;
import biz.softfor.util.api.DeleteRequest;
import biz.softfor.util.api.Identifiable;
import biz.softfor.util.api.InternalResponse;
import biz.softfor.util.api.ReadRequest;
import biz.softfor.util.api.ServerError;
import biz.softfor.util.api.UpdateRequest;
import biz.softfor.util.api.filter.FilterId;
import biz.softfor.util.security.IgnoreAccess;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class CrudSvc<
  K extends Number
, E extends Identifiable<K>
, WOR extends Identifiable<K>
, F extends FilterId<K>
> extends AbstractCrudSvc<K, E, WOR> {

  @PersistenceContext
  protected EntityManager em;

  @Autowired
  protected I18n i18n;

  @Autowired
  protected Validator validator;

  @Transactional
  @Override
  public CommonResponse<WOR> create(CreateRequest<K, WOR> request) {
    em.persist(request.data);
    ColumnDescr.create
    (request.data, (Class<Identifiable<? extends Number>>)clazz(), em);
    return new CommonResponse<>(List.of((WOR)request.data), 1L);
  }

  @Transactional
  public CommonResponse delete(DeleteRequest<K, F> request) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    Class<Identifiable<? extends Number>> clazz
    = (Class<Identifiable<? extends Number>>)clazz();
    PredicateProvider pvdr = new PredicateProvider
    (PredicateProvider.DELETE, cb, idClass(), clazz, request.filter);
    try {
      return new CommonResponse(ColumnDescr.delete
      ((Class<Identifiable<? extends Number>>)classWor(), pvdr, em, cb));
    }
    catch(IllegalAccessException | InvocationTargetException
    | NoSuchMethodException ex) {
      throw new ServerError(ex);
    }
  }

  @Transactional(readOnly = true)
  public CommonResponse<E> read(ReadRequest<K, F> request) {
    try {
      Class<E> clazz = clazz();
      return QueryGraph.read(em, request, clazz).toResponse(clazz);
    }
    catch(IntrospectionException | ReflectiveOperationException ex) {
      throw new ServerError(ex);
    }
  }

  @Transactional
  public CommonResponse update(UpdateRequest<K, F, WOR> request) {
    try {
      CriteriaBuilder cb = em.getCriteriaBuilder();
      PredicateProvider ppNext = new PredicateProvider
      (PredicateProvider.UPDATE, cb, idClass(), clazz(), request.filter);
      InternalResponse result = new InternalResponse();
      result.setTotal
      (updateInternal((WOR)request.data, request.fields, "", cb, ppNext, result));
      return result;
    }
    catch(IllegalAccessException | InstantiationException
    | InvocationTargetException | NoSuchMethodException ex) {
      throw new ServerError(ex);
    }
  }

  @IgnoreAccess
  @Override
  public int updateInternal(
    WOR data
  , List<String> updateToNull
  , String parentField
  , CriteriaBuilder cb
  , PredicateProvider pvdr
  , InternalResponse response
  ) throws IllegalAccessException, InstantiationException
  , InvocationTargetException, NoSuchMethodException {
    int result = 0;
    Class<WOR> classWor = classWor();
    List<String> updateToNullNext
    = StringUtil.filterByPrefix(updateToNull, parentField);
    UpdateCtx ctx = new UpdateCtx
    (em, cb, pvdr, classWor, updateToNullNext, parentField, response);
    CriteriaUpdate<WOR> cru = cb.createCriteriaUpdate((Class<WOR>)ctx.rootClass);
    for(ColumnDescr cd : ColumnDescr.getCds(classWor)) {
      if(!Identifiable.ID.equals(cd.name)
      && (cd.isPlain() || cd instanceof ManyToOneWorDescr)) {
        Object v = data == null ? null : PropertyUtils.getProperty(data, cd.name);
        if(v != null || updateToNull != null
        && updateToNull.contains(StringUtil.field(parentField, cd.name))) {
          cru.set(cd.name, v);
          ctx.response.isEmptyForUpdate(false);
          if(v != null) {
            ctx.response.isEmptyForUpdateToNotNull(false);
          }
        }
      }
    }
    ctx.pvdr.where(cru);
    if(!ctx.response.isEmptyForUpdate()) {
      ctx.count = em.createQuery(cru).executeUpdate();
      result += ctx.count;
//update users set password='updateUpdated' where FILTER
//
//update partners set address='updateUpdated' where FILTER
//
//update personDetails set passportSeries='U386' where id in(
//  select id from partners where FILTER)
//
//update partnerDetails set note='Updated_note392' where id in(
//  select id from partners where FILTER)
//update partnerDetails set note='NNNN' where partnerDetails.id in (
//  (select pd1_0.id from partners p1_0 left join partnerDetails pd1_0 on pd1_0.id=p1_0.id where FILTER))
    }
    if(ctx.response.isEmptyForUpdate() || ctx.count > 0) {
      result += ColumnDescr.update(data, ctx);
    }
    return result;
  }

  @IgnoreAccess
  public void validateUpdate(UpdateRequest request) {
    Set<ConstraintViolation<?>> validate = new HashSet<>();
    ColumnDescr.validateAll
    (validator, validate, classWor(), request.data, request.fields);
    if(CollectionUtils.isNotEmpty(validate)) {
      throw new ConstraintViolationException(validate);
    }
  }

}
