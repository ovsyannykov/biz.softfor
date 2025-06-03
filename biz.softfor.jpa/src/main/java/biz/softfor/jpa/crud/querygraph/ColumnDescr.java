package biz.softfor.jpa.crud.querygraph;

import biz.softfor.jpa.filter.FilterUtil;
import biz.softfor.jpa.filter.Stmt;
import biz.softfor.util.Generated;
import biz.softfor.util.Inflector;
import biz.softfor.util.Reflection;
import biz.softfor.util.StringUtil;
import biz.softfor.util.api.ClientError;
import biz.softfor.util.api.Identifiable;
import biz.softfor.util.api.ServerError;
import biz.softfor.util.api.filter.FilterId;
import biz.softfor.util.security.FieldRoleCalc;
import biz.softfor.util.security.UpdateFieldRoleCalc;
import jakarta.persistence.EntityManager;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.metamodel.EntityType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class ColumnDescr {

  private final static Map<String, EntityInf> entityInfs = new HashMap<>();
  private final static Map<String, Map<String, ColumnDescr>> filterCds
  = new HashMap<>();
  private final static Map<String, Map<String, ColumnDescr>> plainCds
  = new HashMap<>();
  private final static Map<String, Map<String, ColumnDescr>> relationKeys
  = new HashMap<>();

  private final static boolean DEBUG = false;

  public static void init(EntityManager em) throws ReflectiveOperationException {
    Set<EntityType<?>> entities = em.getMetamodel().getEntities();
    for(EntityType<?> et : entities) {
      Class<?> clazz = et.getJavaType();
      boolean isM2MLink = clazz.isAnnotationPresent(ManyToManyGeneratedLink.class);
      if(!clazz.isAnnotationPresent(Generated.class)
      || isM2MLink
      || clazz.isAnnotationPresent(TestEntity.class)) {
        Map<String, ColumnDescr> clazzCds = new HashMap<>();
        Map<String, ColumnDescr> clazzFilterCds = new HashMap<>();
        Map<String, ColumnDescr> clazzPlainCds = new HashMap<>();
        Map<String, ColumnDescr> clazzRelationKeys = new HashMap<>();
        Map<String, ColumnDescr> worCds = new HashMap<>();
        Map<String, ColumnDescr> worRelationKeys = new HashMap<>();
        for(Field field : Reflection.declaredProperties(clazz)) {
          if(!Reflection.HIBERNATE_PROXY.equals(field.getName())) {
            ColumnDescr cd;
            if(field.isAnnotationPresent(OneToOne.class)) {
              cd = new OneToOneColumnDescr(clazz, field);
              clazzRelationKeys.put(cd.name, cd);
              worRelationKeys.put(cd.name, cd);
            } else if(field.isAnnotationPresent(ManyToOne.class)) {
              cd = new ManyToOneColumnDescr(clazz, field);
              ManyToOneKeyDescr kd = new ManyToOneKeyDescr(clazz, field);
              clazzCds.put(kd.name, kd);
              clazzRelationKeys.put(kd.name, kd);
              ManyToOneWorDescr worKd = new ManyToOneWorDescr(clazz, field);
              worCds.put(worKd.name, worKd);
              worRelationKeys.put(worKd.name, worKd);
            } else if(field.isAnnotationPresent(OneToMany.class)) {
              cd = new OneToManyColumnDescr(clazz, field);
              OneToManyKeyDescr kd = new OneToManyKeyDescr(clazz, field);
              clazzCds.put(kd.name, kd);
              clazzRelationKeys.put(kd.name, kd);
              worCds.put(kd.name, kd);
              worRelationKeys.put(kd.name, kd);
            } else if(field.isAnnotationPresent(ManyToMany.class)) {
              cd = new ManyToManyColumnDescr(clazz, field);
              ManyToManyKeyDescr kd = new ManyToManyKeyDescr(clazz, field);
              clazzCds.put(kd.name, kd);
              clazzRelationKeys.put(kd.name, kd);
              worCds.put(kd.name, kd);
              worRelationKeys.put(kd.name, kd);
            } else {
              cd = new ColumnDescr(clazz, field);
              clazzPlainCds.put(cd.name, cd);
            }
            clazzCds.put(cd.name, cd);
            clazzFilterCds.put(cd.name, cd);
            worCds.put(cd.name, cd);
          }
        }
        String className = clazz.getName();
        entityInfs.put(className, new EntityInf(clazz, clazzCds));
        plainCds.put(className, clazzPlainCds);
        relationKeys.put(className, clazzRelationKeys);
        if(!isM2MLink) {
          String worName = Reflection.worClassName(className);
          entityInfs.put(worName, new EntityInf(clazz, worCds));
          plainCds.put(worName, clazzPlainCds);
          relationKeys.put(worName, worRelationKeys);
          filterCds.put(Reflection.filterClassName(clazz), clazzFilterCds);
        }
      }
    }
    if(DEBUG) {
      String[] keys;
      System.out.println("\ncds (" + entityInfs.keySet().size() + ")\n" + "=".repeat(32));
      keys = entityInfs.keySet().toArray(String[]::new);
      Arrays.sort(keys, (s1, s2) -> s1.substring(s1.lastIndexOf('.')).compareTo(s2.substring(s2.lastIndexOf('.'))));
      for(String c : keys) {
        System.out.println("  " + c + ": " + String.join(", ", entityInfs.get(c).cds.keySet()));
      }
      System.out.println("\nplainCds (" + plainCds.keySet().size() + ")\n" + "=".repeat(32));
      keys = plainCds.keySet().toArray(String[]::new);
      Arrays.sort(keys, (s1, s2) -> s1.substring(s1.lastIndexOf('.')).compareTo(s2.substring(s2.lastIndexOf('.'))));
      for(String c : keys) {
        System.out.println("  " + c + ": " + String.join(", ", plainCds.get(c).keySet()));
      }
      System.out.println("\nrelationKeys (" + relationKeys.keySet().size() + ")\n" + "=".repeat(32));
      keys = relationKeys.keySet().toArray(String[]::new);
      Arrays.sort(keys, (s1, s2) -> s1.substring(s1.lastIndexOf('.')).compareTo(s2.substring(s2.lastIndexOf('.'))));
      for(String c : keys) {
        System.out.println("  " + c + ": " + String.join(", ", relationKeys.get(c).keySet()));
      }
      System.out.println("\nfilterCds (" + filterCds.keySet().size() + ")\n" + "=".repeat(32));
      keys = filterCds.keySet().toArray(String[]::new);
      Arrays.sort(keys, (s1, s2) -> s1.substring(s1.lastIndexOf('.')).compareTo(s2.substring(s2.lastIndexOf('.'))));
      for(String c : keys) {
        System.out.println("  " + c + ": " + String.join(", ", filterCds.get(c).keySet()));
      }
    }
  }

  public static <K extends Number, E extends Identifiable<K>> E copyByFields
  (E source, Class<E> clazz, Collection<String> fields)
  throws IllegalAccessException, InstantiationException
  , InvocationTargetException, NoSuchMethodException {
    E result = null;
    if(source != null) {
      result = clazz.getConstructor().newInstance();
      copyField(result, source, Identifiable.ID);
      if(CollectionUtils.isEmpty(fields)) {
        copyField(result, source);
      } else {
        for(String f : fields) {
          copyField(result, source, f.split(StringUtil.FIELDS_DELIMITER_REGEX), 0);
        }
      }
    }
    return result;
  }

  public static void create(
    Identifiable<? extends Number> data
  , Class<Identifiable<? extends Number>> clazz
  , EntityManager em
  ) {
    try {
      for(ColumnDescr cd : getRelationKeys(clazz)) {
        Object v = PropertyUtils.getProperty(data, cd.name);
        if(v != null) {
          ((RelationKeyDescr)cd).create(v, data, em);
        }
      }
    } catch(IllegalAccessException | InvocationTargetException
    | InstantiationException | NoSuchMethodException ex) {
      throw new ServerError(ex);
    }
  }

  public static int delete(
    Class<Identifiable<? extends Number>> classWor
  , PredicateProvider pvdr
  , EntityManager em
  , CriteriaBuilder cb
  ) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    int total = 0;
    //Collection<ColumnDescr> rks = getRelationKeys(classWor);
    for(ColumnDescr cd : getRelationKeys(classWor)) {
      total += ((RelationKeyDescr)cd).delete(classWor, em, cb, pvdr);
    }
    CriteriaDelete<?> crd = cb.createCriteriaDelete(classWor);
    pvdr.where(crd);
    return em.createQuery(crd).executeUpdate() + total;
//delete from partners where FILTER
  }

  public static <K extends Number> DiffContext diff(
    String parentField
  , Class<?> parentClass
  , Identifiable<K> itemWor
  , Identifiable<K> itemWorOld
  ) throws IllegalAccessException, IllegalArgumentException
  , InstantiationException, InvocationTargetException, NoSuchMethodException {
    DiffContext result = new DiffContext();
    for(ColumnDescr cd : get(parentClass).values()) {
      if(!Identifiable.ID.equals(cd.name) && cd.isPlainOrRelationKey()) {
        Object v = itemWor == null
        ? null : PropertyUtils.getProperty(itemWor, cd.name);
        Object vOld = itemWorOld == null
        ? null : PropertyUtils.getProperty(itemWorOld, cd.name);
        cd.diff(parentClass, parentField, v, vOld, result);
      }
    }
    return result;
  }

  public static Map<String, ColumnDescr> get(Class<?> clazz) {
    return getInf(clazz).cds;
  }

  public static EntityInf getInf(Class<?> clazz) {
    String className = clazz.getName();
    EntityInf result = entityInfs.get(className);
    if(result == null) {
      throw new ServerError
      ("The Entity Info for the class " + className + " not found.");
    }
    return result;
  }

  public static Collection<ColumnDescr> getCds(Class<?> clazz) {
    return get(clazz).values();
  }

  public static Path<?> getPath(From root, String fieldName) {
    Map<String, ColumnDescr> cds = get(root.getJavaType());
    ColumnDescr cd = cds.get(fieldName);
    return cd.getPath(root);
  }

  public static Collection<ColumnDescr> getPlainCds(Class<?> clazz) {
    return get(plainCds, clazz).values();
  }

  public static void initOneToOnes(Object data, Class<?> dataClass)
  throws IllegalAccessException, InstantiationException
  , InvocationTargetException, NoSuchMethodException {
    Collection<ColumnDescr> cds = getRelationKeys(dataClass);
    if(cds != null) {
      for(ColumnDescr cd : cds) {
        if(cd.isOneToOne()) {
          Object v = PropertyUtils.getProperty(data, cd.fieldName);
          if(v == null) {
            v = cd.clazz.getConstructor().newInstance();
            PropertyUtils.setProperty(data, cd.fieldName, v);
          }
          initOneToOnes(v, cd.clazz);
        }
      }
    }
  }

  public static String manyToOneKeyName(Field field) {
    return field.getAnnotation(JoinColumn.class).name();
  }

  public static String toManyKeyName(String names) {
    return Inflector.getInstance().singularize(names) + "Ids";
  }

  public static void validate(
    Validator validator
  , Set<ConstraintViolation<?>> validate
  , Class<?> classWor
  , Object data
  , List<String> fields
  ) {
    if(CollectionUtils.isNotEmpty(fields)) {
      if(data == null) {
        data = Reflection.newInstance(classWor);
      }
      List<Class<?>> validationGroups = new ArrayList<>();
      try {
        for(ColumnDescr cd : getCds(classWor)) {
          if(cd.isWor()) {
            if(fields.contains(cd.name) && cd.validationGroup != null) {
              validationGroups.add(cd.validationGroup);
            }
            if(cd.isOneToOne()) {
              Class<?> cdWorClass = Reflection.worClass(cd.clazz);
              Object dataNext = PropertyUtils.getProperty(data, cd.name);
              List<String> fieldsNext = StringUtil.filterByPrefix(fields, cd.name);
              validate(validator, validate, cdWorClass, dataNext, fieldsNext);
            }
          }
        }
      } catch(ClassNotFoundException | IllegalAccessException
      | InvocationTargetException | NoSuchMethodException ex) {
        throw new ServerError(ex);
      }
      validate.addAll
      (validator.validate(data, validationGroups.toArray(Class[]::new)));
    }
  }

  public static void validateAll(
    Validator validator
  , Set<ConstraintViolation<?>> validate
  , Class<?> classWor
  , Object data
  , List<String> nullableFields
  ) {
    if(data == null) {
      data = Reflection.newInstance(classWor);
    }
    List<Class<?>> validationGroups = new ArrayList<>();
    try {
      for(ColumnDescr cd : getCds(classWor)) {
        if(!Identifiable.ID.equals(cd.name) && cd.isWor()) {
          Object dataNext = PropertyUtils.getProperty(data, cd.name);
          if(dataNext != null
          || nullableFields != null && nullableFields.contains(cd.name)) {
            validationGroups.add(cd.validationGroup);
          }
          if(cd.isOneToOne()) {
            Class<?> cdWorClass = Reflection.worClass(cd.clazz);
            List<String> nullableFieldsNext
            = StringUtil.filterByPrefix(nullableFields, cd.name);
            validateAll
            (validator, validate, cdWorClass, dataNext, nullableFieldsNext);
          }
        }
      }
    } catch(ClassNotFoundException | IllegalAccessException
    | InvocationTargetException | NoSuchMethodException ex) {
      throw new ServerError(ex);
    }
    validate.addAll
    (validator.validate(data, validationGroups.toArray(Class[]::new)));
  }

  public static int update(Identifiable<?> data, UpdateCtx ctx)
  throws IllegalAccessException, InvocationTargetException
  , InstantiationException, NoSuchMethodException {
    int result = 0;
    for(ColumnDescr cd : getRelationKeys(ctx.rootClass)) {
      ctx.isUpdateToNull = ctx.updateToNull != null
      && (ctx.isUpdateToNullField(cd.name) || ctx.isUpdateToNullSubfield(cd.name));
      Object v = data == null ? null : PropertyUtils.getProperty(data, cd.name);
      if(v != null || ctx.isUpdateToNull) {
        result += ((RelationKeyDescr)cd).update(v, ctx);
      }
    }
    return result;
  }

  public static Predicate where
  (FilterId filter, Predicate result, From<?, ?> from, CriteriaBuilder cb) {
    if(filter != null) {
      result = (Predicate)new Stmt(from, cb).value(filter.and());
      try {
        for(ColumnDescr cd : filterCds.get(filter.getClass().getName()).values()) {
          result = FilterUtil.where(cd.name, filter, result, from, cb);
        }
      }
      catch(IllegalAccessException | InvocationTargetException
      | NoSuchMethodException ex) {
        throw new ClientError(ex);
      }
    }
    return result;
  }

  public final String fieldName;
  final Class<?> fieldClass;
  public final String name;
  public final Class<?> clazz;
  public final boolean required;
  public final long roleId;
  public final long updateRoleId;
  public final Class<?> validationGroup;

  protected ColumnDescr
  (Class<?> parent, Field field, String name, Class<?> clazz) {
    fieldName = field.getName();
    fieldClass = field.getType();
    this.name = name;
    this.clazz = clazz;
    boolean lRequired = false;
    for(Class<? extends Annotation> vac : new Class[]
    { NotNull.class, NotBlank.class, NotEmpty.class }) {
      if(field.isAnnotationPresent(vac)) {
        lRequired = true;
        break;
      }
    }
    required = lRequired;
    roleId = new FieldRoleCalc(field).id();
    updateRoleId = new UpdateFieldRoleCalc(field).id();
    if(Identifiable.ID.equals(name)) {
      validationGroup = null;
    } else {
      String worName = Reflection.worClassName(parent.getName());
      String vgName = worName + "$" + StringUtils.capitalize(name);
      Class<?> vg;
      try {
        vg = Class.forName(vgName);
      } catch(ClassNotFoundException ex) {
        vg = null;
      }
      validationGroup = vg;
    }
  }

  protected ColumnDescr(Class<?> parent, Field field, Class<?> clazz) {
    this(parent, field, field.getName(), clazz);
  }

  protected ColumnDescr(Class<?> parent, Field field) {
    this(parent, field, field.getType());
  }

  protected ColumnDescr(ColumnDescr cd) {
    fieldName = cd.fieldName;
    fieldClass = cd.fieldClass;
    name = cd.name;
    clazz = cd.clazz;
    required = cd.required;
    roleId = cd.roleId;
    updateRoleId = cd.updateRoleId;
    validationGroup = cd.validationGroup;
  }

  @Override
  public String toString() {
    return "name=" + name + ", clazz=" + clazz.getName();
  }

  public final boolean isOneToOne() {
    return this instanceof OneToOneColumnDescr;
  }

  public final boolean isPlain() {
    return !(this instanceof RelationColumnDescr)
    && !(this instanceof RelationKeyDescr);
  }

  public final boolean isPlainOrRelationKey() {
    return this instanceof RelationKeyDescr
    ||
    !(this instanceof RelationColumnDescr) && !(this instanceof RelationKeyDescr);
  }

  public final boolean isWor() {
    return !(this instanceof ManyToOneColumnDescr)
    && !(this instanceof OneToManyColumnDescr)
    && !(this instanceof ManyToManyColumnDescr);
  }

  void addTo(AbstractQueryGraph queryGraph, String[] parts, int partIdx) {
    queryGraph.addToSelections(this);
  }

  protected final void addTo(AbstractQueryGraph queryGraph) {
    queryGraph.addToSelections(this);
  }

  final protected Identifiable<?> copy(Object result, Identifiable<?> source)
  throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
    if(result == null) {
      result = clazz.getConstructor().newInstance();
    }
    copyField(result, source, Identifiable.ID);
    return (Identifiable<?>)result;
  }

  protected void copy
  (Object result, Object source, String[] fieldParts, int deep)
  throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
    copy(result, source, fieldParts[deep]);
  }

  protected void diff(
    Class<?> parentClass
  , String parentField
  , Object v
  , Object vOld
  , DiffContext result
  ) throws IllegalAccessException, IllegalArgumentException
  , InstantiationException, InvocationTargetException, NoSuchMethodException {
    if(Objects.equals(v, vOld)) {
      if(v != null) {
        result.all2Null = false;
      }
    } else {
      result.changed = true;
      if(v == null) {
        result.updateToNull(StringUtil.field(parentField, name));
      } else {
        result.setProperty(parentClass, name, v);
      }
    }
  }

  protected Path<?> getPath(From root) {
    return root.get(name);
  }

  private static Map<String, ColumnDescr> get
  (Map<String, Map<String, ColumnDescr>> map, Class<?> clazz) {
    String className = clazz.getName();
    Map<String, ColumnDescr> result = map.get(className);
    if(result == null) {
      throw new ServerError
      ("The column descriptors for the class " + className + " not found.");
    }
    return result;
  }

  private static Collection<ColumnDescr> getRelationKeys(Class<?> clazz) {
    return get(relationKeys, clazz).values();
  }

  protected static void copyField
  (Object result, Object source, String[] fieldParts, int deep)
  throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
    if(deep < fieldParts.length) {
      ColumnDescr cd = get(result.getClass()).get(fieldParts[deep]);
      cd.copy(result, source, fieldParts, deep);
    } else {
      copyField(result, source);
    }
  }

  private static void copyField(Object result, Object source)
  throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
    for(ColumnDescr cd : getPlainCds(result.getClass())) {
      copy(result, source, cd.name);
    }
  }

  private static void copy(Object result, Object source, String field)
  throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
    if(!Identifiable.ID.equals(field)) {
      copyField(result, source, field);
    }
  }

  private static void copyField(Object result, Object source, String field)
  throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    Object v = PropertyUtils.getProperty(source, field);
    PropertyUtils.setProperty(result, field, v);
    if(DEBUG) {
      String className = source.getClass().getSimpleName();
      System.out.println(className + "#" + field + "=" + v);
    }
  }

}
