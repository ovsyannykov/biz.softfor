package biz.softfor.jpa.crud;

import biz.softfor.jpa.crud.querygraph.PredicateProvider;
import biz.softfor.util.Reflection;
import biz.softfor.util.api.CommonResponse;
import biz.softfor.util.api.CreateRequest;
import biz.softfor.util.api.Identifiable;
import biz.softfor.util.api.InternalResponse;
import biz.softfor.util.api.ServerError;
import biz.softfor.util.security.IgnoreAccess;
import jakarta.persistence.criteria.CriteriaBuilder;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractCrudSvc
<K extends Number, E extends Identifiable<K>, WOR extends Identifiable<K>> {

  public final static String CREATE_METHOD = "create";
  public final static String DELETE_METHOD = "delete";
  public final static String READ_METHOD = "read";
  public final static String UPDATE_METHOD = "update";

  public final static String Update_multiple_records_with_OneToMany_items
  = "Update multiple ({0}) records with OneToMany {1} IDs.";
  public final static String X_TO_MANY_UPDATE_ERROR
  = "{0} records with '"+ Identifiable.ID + "' in[{3}] should be updated in the"
  + " ''{2}'' entity(-ies), but {1} were updated.";

  public static AbstractCrudSvc service(Class<?> clazz) {
    AbstractCrudSvc result = services.get(clazz);
    if(result == null) {
      throw new ServerError("CRUD service for " + clazz.getName() + " not found.");
    }
    return result;
  }

  private final Class<K> idClass;
  private final Class<E> clazz;
  private final Class<WOR> classWor;
  private final Class<?> serviceClass;

  private final static Map<Class, AbstractCrudSvc> services
  = new ConcurrentHashMap<>();

  protected AbstractCrudSvc() {
    idClass = Reflection.superGenericParameter(getClass(), 0);
    clazz = Reflection.superGenericParameter(getClass(), 1);
    classWor = Reflection.superGenericParameter(getClass(), 2);
    serviceClass = getClass();
    services.put(clazz, this);
  }

  @IgnoreAccess
  public Class<?> serviceClass() {
    return serviceClass;
  }

  @IgnoreAccess
  public Class<K> idClass() {
    return idClass;
  }

  @IgnoreAccess
  public Class<E> clazz() {
    return clazz;
  }

  @IgnoreAccess
  public Class<WOR> classWor() {
    return classWor;
  }

  public abstract CommonResponse<WOR> create(CreateRequest request);

  @IgnoreAccess
  public abstract int updateInternal(
    WOR data
  , List<String> updateToNull
  , String parentField
  , CriteriaBuilder cb
  , PredicateProvider pvdr
  , InternalResponse response
  ) throws IllegalAccessException, InstantiationException
  , InvocationTargetException, NoSuchMethodException;

}
