package biz.softfor.user.spring;

import biz.softfor.i18nspring.I18n;
import biz.softfor.jpa.crud.AbstractCrudSvc;
import biz.softfor.jpa.crud.querygraph.ColumnDescr;
import biz.softfor.user.jpa.Role;
import biz.softfor.user.jpa.RoleWor;
import biz.softfor.user.jpa.Role_;
import biz.softfor.user.jpa.UserGroup;
import biz.softfor.user.jpa.UserGroup_;
import biz.softfor.util.Constants;
import biz.softfor.util.Json;
import biz.softfor.util.StringUtil;
import biz.softfor.util.api.AbstractRequest;
import biz.softfor.util.api.BasicResponse;
import biz.softfor.util.api.ClientError;
import biz.softfor.util.api.CreateRequest;
import biz.softfor.util.api.Identifiable;
import biz.softfor.util.api.ReadRequest;
import biz.softfor.util.api.UpdateRequest;
import biz.softfor.util.security.MethodRoleCalc;
import biz.softfor.util.security.ActionAccess;
import biz.softfor.util.security.DefaultAccess;
import biz.softfor.util.security.UpdateAccess;
import biz.softfor.util.security.UpdateClassRoleCalc;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Table;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.java.Log;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Component
@Log
public class SecurityMgr {

  public final static String Access_to_fields_denied = "Access_to_fields_denied";
  public final static String Access_to_items_denied = "Access_to_items_denied";
  public final static String Access_to_method_denied = "Access_to_method_denied";
  public final static String Fields_contains_empty_item = "Fields_contains_empty_item";
  public final static String Fields_contains_invalid_item = "Fields_contains_invalid_item";
  public final static String Fields_contains_invalid_items = "Fields_contains_invalid_items";
  public final static String Fields_contains_not_plain_column = "Fields_contains_not_plain_column";

  public final int pageSize;
  public final Map<Long, RoleData> rolesData;
  int version;
  final Map<Long, ParentRoles> member2Parent;
  final Map<Long, List<Long>> parent2Members;
  private final I18n i18n;

  public final static boolean DEBUG = true;

  public static boolean isAuthorized(String role) {
    return !role.equals(Constants.ROLE_ANONYMOUS);
  }

  public static boolean isAuthorized(Collection<String> roles) {
    return roles != null
    && (roles.size() != 1 || isAuthorized(roles.iterator().next()));
  }

  public SecurityMgr(
    @Value("${biz.softfor.spring.security.pageSize:2000}") int pageSize
  , List<ScanRoles> loaders
  , EntityManager em
  , PlatformTransactionManager tm
  , I18n i18n
  ) {
    this.pageSize = pageSize;
    version = Integer.MIN_VALUE;
    member2Parent = new HashMap<>();
    parent2Members = new HashMap<>();
    this.i18n = i18n;

    Map<Long, Role> fromCode = new HashMap<>();
    for(ScanRoles l : loaders) {
      l.load(fromCode, member2Parent, parent2Members);
    }
    if(DEBUG) {
      String result = "Roles fromCode: " + fromCode.size()
      + "\n" + "=".repeat(16) + "\n" + TO_SQL_HEAD;
      for(Role role : fromCode.values()) {
        result += toSqlInsert(role);
      }
      System.out.println(result);
    }

    CriteriaBuilder cb = em.getCriteriaBuilder();
    Map<Long, Role> fromDb;
    {
      CriteriaQuery<Role> cq = cb.createQuery(Role.class);
      cq.from(Role.class);
      List<Role> res = new TransactionTemplate(tm).execute
      (status -> em.createQuery(cq).getResultList());
      if(DEBUG) {
        String result = "Loaded from DB roles: " + res.size()
        + "\n" + "=".repeat(16) + "\n" + TO_SQL_HEAD;
        for(Role role : res) {
          result += toSqlInsert(role);
        }
        System.out.println(result);
      }
      fromDb = new HashMap<>(fromCode.size());
      for(Role role : res) {
        fromDb.put(role.getId(), role);
      }
    }

    List<Role> inserts = new ArrayList<>();
    for(Role role : fromCode.values()) {
      Role roleFromDb = fromDb.get(role.getId());
      if(roleFromDb == null) {
        inserts.add(role);
      }
    }
    if(DEBUG) {
      String result = "Inserted roles: " + inserts.size()
      + "\n" + "=".repeat(16) + "\n" + TO_SQL_HEAD;
      for(Role role : inserts) {
        result += toSqlInsert(role);
      }
      System.out.println(result);
    }
    new TransactionTemplate(tm).executeWithoutResult(status -> {
      for(Role role : inserts) {
        em.persist(role);
      }
    });

    List<Role> orphans = new ArrayList<>();
    List<Role> noLongerOrphans = new ArrayList<>();
    List<Long> updateOrphan = new ArrayList<>();
    for(Role roleFromDb : fromDb.values()) {
      Role roleFromCode = fromCode.get(roleFromDb.getId());
      if(roleFromCode == null) {
        orphans.add(roleFromDb);
        if(!roleFromDb.getOrphan()) {
          updateOrphan.add(roleFromDb.getId());
        }
      } else {
        if(roleFromDb.getOrphan()) {
          noLongerOrphans.add(roleFromDb);
          updateOrphan.add(roleFromDb.getId());
        }
      }
    }
    if(!updateOrphan.isEmpty()) {
      log.warning(() -> {
        String result = "\norphans: " + orphans.size()
        + "\n" + "=".repeat(16) + "\n" + TO_SQL_HEAD;
        for(Role role : orphans) {
          result += toSqlInsert(role);
        }
        return result;
      });
      CriteriaUpdate cu = cb.createCriteriaUpdate(Role.class);
      Root root = cu.getRoot();
      cu.where(root.get(Role_.ID).in(updateOrphan));
      cu.set(Role_.ORPHAN, cb.not(root.get(Role_.ORPHAN)));
      new TransactionTemplate(tm).executeWithoutResult
      (status -> em.createQuery(cu).executeUpdate());
    }
    if(DEBUG) {
      if(!noLongerOrphans.isEmpty()) {
        String result = "No longer orphaned roles: " + noLongerOrphans.size()
        + "\n" + "=".repeat(16) + "\n" + TO_SQL_HEAD;
        for(Role role : noLongerOrphans) {
          result += toSqlInsert(role);
        }
        System.out.println(result);
      }
    }

    if(DEBUG) {
      String changes = "";
      for(Role roleFromCode : fromCode.values()) {
        Role roleFromDb = fromDb.get(roleFromCode.getId());
        if(roleFromDb != null) {
          String msg = "";
          if(!roleFromCode.getName().equals(roleFromDb.getName())) {
            msg += ", " + Role_.NAME + "=" + roleFromDb.getName();
          }
          if(!roleFromCode.getDescription().equals(roleFromDb.getDescription())) {
            msg += ", " + Role_.DESCRIPTION + "="
            + roleFromDb.getDescription();
          }
          if(!roleFromCode.getDefaultAccess().equals(roleFromDb.getDefaultAccess())) {
            msg += ", " + Role_.DEFAULT_ACCESS + "="
            + roleFromDb.getDefaultAccess();
          }
          if(!msg.isEmpty()) {
            Class<?> annoClass = roleFromDb.getUpdateFor()
            ? UpdateAccess.class : ActionAccess.class;
            changes += "\n" + annoClass.getSimpleName() + "("
            + Role_.OBJ_NAME + "=" + roleFromDb.getObjName() + msg + ")";
          }
        }
      }
      if(!changes.isEmpty()) {
        System.out.println("Changed by users:\n" + "=".repeat(16) + changes + "\n");
      }
    }

    rolesData = new HashMap<>();
    {
      CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
      Root<Role> root = cq.from(Role.class);
      cq.select(cb.tuple(
        root.get(Role_.ID)//0
      , root.get(Role_.DENIED_FOR_ALL)//1
      , root.get(Role_.UPDATE_FOR)//2
      , root.get(Role_.IS_URL)//3
      , root.get(Role_.DEFAULT_ACCESS)//4
      , root.get(Role_.OBJ_NAME)//5
      , root.join(Role_.GROUPS, JoinType.LEFT).get(UserGroup_.NAME)//6
      ));
      cq.where(cb.and(cb.equal(root.get(Role_.ORPHAN), false)));
      cq.orderBy(cb.asc(root.get(Role_.ID)));
      List<Tuple> res = new TransactionTemplate(tm).execute
      (status -> em.createQuery(cq).setMaxResults(pageSize).getResultList());
      Set<String> groups = new HashSet<>();
      for(int iLast = res.size() - 1, i = 0; i <= iLast; ++i) {
        Tuple t = res.get(i);
        String group = t.get(6, String.class);
        if(group != null) {
          groups.add(Constants.ROLE_PREFIX + group);
        }
        Long id = t.get(0, Long.class);
        if(i == iLast || !id.equals(res.get(i + 1).get(0, Long.class))) {
          boolean deniedForAll = t.get(1, Boolean.class);
          boolean updateFor = t.get(2, Boolean.class);
          boolean isUrl = t.get(3, Boolean.class);
          DefaultAccess defaultAccess = t.get(4, DefaultAccess.class);
          String objName = t.get(5, String.class);
          RoleData rd = new RoleData
          (id, deniedForAll, updateFor, isUrl, defaultAccess, objName, groups);
          rolesData.put(id, rd);
          groups = new HashSet<>();
        }
      }
      if(DEBUG) {
        String out = "\nparent2Members:" + parent2Members.size() + "\n" + "=".repeat(16);
        for(Map.Entry<Long, List<Long>> p2m : parent2Members.entrySet()) {
          out += "\n" + rolesData.get(p2m.getKey()).toStr() + ":";
          for(Long v : p2m.getValue()) {
            out += " " + rolesData.get(v).toStr() + ",";
          }
        }
        System.out.println(out);
        out = "\nmember2Parent:" + member2Parent.size() + "\n" + "=".repeat(16);
        for(Map.Entry<Long, ParentRoles> m2p : member2Parent.entrySet()) {
          out += "\n" + rolesData.get(m2p.getKey()).toStr() + ": "
          + rolesData.get(m2p.getValue().host()).toStr();
          Long t = m2p.getValue().type();
          if(t != null) {
            out += ", " + rolesData.get(t).toStr();
          }
        }
        System.out.println(out);
      }
      recount();
    }
  }

  public final void addGroup
  (Integer groupId, Collection<Long> roleIds, EntityManager em) {
    List<String> groups = readGroupNames(List.of(groupId), em);
    for(Long id : roleIds) {
      RoleData rd = rolesData.get(id);
      rd.access.groups.addAll(groups);
    }
    recount();
  }

  public final void createCheck
  (AbstractCrudSvc service, Collection<String> groups) {
    methodCheck(service.serviceClass(), AbstractCrudSvc.CREATE_METHOD, groups);
    if(!isAllowed(new UpdateClassRoleCalc(service.clazz()).id(), groups)) {
      throw new ClientError(
        i18n.message(
          Access_to_items_denied
        , i18n.message(((Table)service.clazz().getAnnotation(Table.class)).name())
        )
      , null
      , BasicResponse.ACCESS_DENIED
      );
    }
  }

  public final void deleteCheck
  (AbstractCrudSvc service, Collection<String> groups) {
    methodCheck(service.serviceClass(), AbstractCrudSvc.DELETE_METHOD, groups);
    if(!isAllowed(new UpdateClassRoleCalc(service.clazz()).id(), groups)) {
      throw new ClientError(
        i18n.message(
          Access_to_items_denied
        , i18n.message(((Table)service.clazz().getAnnotation(Table.class)).name())
        )
      , null
      , BasicResponse.ACCESS_DENIED
      );
    }
  }

  public final boolean isAllowed(long roleId, Collection<String> groups) {
    boolean result;
    RoleData rd = rolesData.get(roleId);
    if(rd == null) {
      result = true;
    } else if(rd.deniedForAll) {
      result = false;
    } else {
      DefaultAccess rdDefaultAccess = rd.effective.defaultAccess;
      Set<String> rdGroups = rd.effective.groups;
      if(CollectionUtils.isEmpty(rdGroups)) {
        result = switch(rdDefaultAccess) {
          case DefaultAccess.EVERYBODY -> true;
          case DefaultAccess.AUTHORIZED -> isAuthorized(groups);
          case DefaultAccess.NOBODY -> false;
        };
      } else if(groups != null) {
        result = CollectionUtils.containsAny(groups, rdGroups);
      } else {
        result = false;
      }
    }
    return result;
  }

  public final boolean isAllowedAccess
  (Class<?> clazz, String property, Collection<String> groups) {
    long roleId = ColumnDescr.get(clazz).get(property).roleId;
    return isAllowed(roleId, groups);
  }

  public final boolean isAllowedUpdate
  (Class<?> clazz, String property, Collection<String> groups) {
    long roleId = ColumnDescr.get(clazz).get(property).updateRoleId;
    return isAllowed(roleId, groups);
  }

  public final boolean isMethodAllowed
  (Class<?> serviceClass, String method, Collection<String> groups) {
    long roleId = new MethodRoleCalc(serviceClass, method).id();
    return isAllowed(roleId, groups);
  }

  public final void methodCheck
  (Class<?> serviceClass, String method, Collection<String> groups) {
    if(!isMethodAllowed(serviceClass, method, groups)) {
      throw new ClientError(
        i18n.message(Access_to_method_denied, method)
      , null
      , BasicResponse.ACCESS_DENIED
      );
    }
  }

  public final void readCheck
  (AbstractCrudSvc service, ReadRequest request, Collection<String> groups) {
    methodCheck(service.serviceClass(), AbstractCrudSvc.READ_METHOD, groups);
    Set<String> denied = new HashSet<>();
    Class<?> clazz = service.clazz();
    if(CollectionUtils.isEmpty(request.fields)) {
      if(request.fields == null) {
        request.fields = new ArrayList<>();
      }
      expandLastRelation(request.fields, denied, clazz, "", groups, false);
    } else {
      List<String> expanded = new ArrayList<>();
      fieldsParse(request.fields, expanded, clazz, denied, groups, false);
      request.fields = expanded;
    }
    checkDenied(denied, i18n);
  }

  public final void removeGroups(Collection<Integer> groupIds, EntityManager em) {
    List<String> groups = readGroupNames(groupIds, em);
    for(RoleData rd : rolesData.values()) {
      rd.access.groups.removeAll(groups);
    }
    recount();
  }

  public final void update
  (Collection<Long> roleIds, RoleWor data, EntityManager em) {
    if(!roleIds.isEmpty()) {
      DefaultAccess defaultAccess = data.getDefaultAccess();
      Set<Integer> groupIds = data.getGroupIds();
      List<String> groups = CollectionUtils.isEmpty(groupIds)
      ? Collections.EMPTY_LIST : readGroupNames(groupIds, em);
      for(Long id : roleIds) {
        RoleData rd = rolesData.get(id);
        if(rd != null) {
          if(defaultAccess != null) {
            rd.access.defaultAccess = defaultAccess;
          }
          if(groupIds != null) {
            rd.access.groups.clear();
            rd.access.groups.addAll(groups);
          }
        }
      }
      recount();
    }
  }

  public final void updateCheck
  (AbstractCrudSvc service, UpdateRequest request, Collection<String> groups) {
    methodCheck(service.serviceClass(), AbstractCrudSvc.UPDATE_METHOD, groups);
    Set<String> denied = new HashSet<>();
    Class<?> classWor = service.classWor();
    updateCheck(request.data, classWor, denied, "", groups);
    fieldsParse(request.fields, null, classWor, denied, groups, true);
    checkDenied(denied, i18n);
  }

  public final void updateGroups
  (Collection<Integer> groupIds, Set<Long> roleIds, EntityManager em) {
    List<String> groups = readGroupNames(groupIds, em);
    for(RoleData rd : rolesData.values()) {
      if(roleIds.contains(rd.id)) {
        if(DEBUG && !rd.access.groups.containsAll(groups)) {
          System.out.println(rd.objName + (rd.updateFor ? "(update)" : "") + ": add " + groups);
        }
        rd.access.groups.addAll(groups);
      } else {
        if(DEBUG && CollectionUtils.containsAny(rd.access.groups, groups)) {
          System.out.println(rd.objName + (rd.updateFor ? "(update)" : "") + ": remove " + groups);
        }
        rd.access.groups.removeAll(groups);
      }
    }
    recount();
  }

  private void recount() {
    if(DEBUG) System.out.println("\neffRecount\n" + "=".repeat(16));
    ++version;
    for(RoleData rd : rolesData.values()) {
      rd.reset();
    }
    Deque<RoleData> parents = new ArrayDeque<>();
    for(RoleData rd : rolesData.values()) {
      rd.recount(this, parents);
    }
    if(DEBUG) {
      ObjectMapper om = Json.objectMapper();
      String out = "\nrolesData: " + rolesData.size() + "\n" + "=".repeat(16);
      for(RoleData rd : rolesData.values()) {
        out += "\n" + Json.serialize(om, rd);
      }
      System.out.println(out);
    }
  }

  private static void checkDenied(Set<String> denied, I18n i18n) {
    if(!denied.isEmpty()) {
      throw new ClientError(
        i18n.message(Access_to_fields_denied, denied.toString())
      , null
      , BasicResponse.ACCESS_DENIED
      );
    }
  }

  private void expandLastRelation(
    Collection<String> fields
  , Set<String> denied
  , Class<?> parent
  , String parentName
  , Collection<String> groups
  , boolean isUpdate
  ) {
    String prefix = parentName;
    if(!prefix.isEmpty()) {
      prefix += StringUtil.FIELDS_DELIMITER;
    }
    for(ColumnDescr cd : ColumnDescr.getCds(parent)) {
      if(cd.isPlain() || cd.isOneToOne()) {
        long roleId = isUpdate ? cd.updateRoleId : cd.roleId;
        if(isAllowed(roleId, groups)) {
          String name = prefix + cd.name;
          if(cd.isPlain()) {
            fields.add(name);
          } else {
            expandLastRelation(fields, denied, cd.clazz, name, groups, isUpdate);
          }
        } else {
          if(parentName.isEmpty()) {
            throw new ClientError(
              i18n.message(BasicResponse.Access_denied)
            , null
            , BasicResponse.ACCESS_DENIED
            );
          } else {
            denied.add(parentName);
            break;
          }
        }
      }
    }
  }

  private void fieldsParse(
    Collection<String> fields
  , Collection<String> expanded
  , Class<?> clazz
  , Set<String> denied
  , Collection<String> groups
  , boolean isUpdate
  ) {
    if(fields != null) {
      for(String f : fields) {
        if(f.isBlank()) {
          throw new ClientError(i18n.message(
            Fields_contains_empty_item, AbstractRequest.FIELDS, fields.toString()
          ));
        }
        Class<?> parentClass = clazz;
        String prefix = "";
        String[] parts = f.split(StringUtil.FIELDS_DELIMITER_REGEX);
        for(int p = 0; p < parts.length; ++p) {
          String fn = parts[p];
          if(fn.isBlank()) {
            throw new ClientError(i18n.message
            (Fields_contains_invalid_items, AbstractRequest.FIELDS, f));
          }
          ColumnDescr cd = ColumnDescr.get(parentClass).get(fn);
          if(cd == null) {
            throw new ClientError(i18n.message
            (Fields_contains_invalid_item, AbstractRequest.FIELDS, f, fn));
          }
          if(!prefix.isEmpty()) {
            prefix += StringUtil.FIELDS_DELIMITER;
          }
          prefix += fn;
          if(cd.isPlainOrRelationKey()) {
            boolean isLast = p == parts.length - 1;
            if(!isLast && !cd.isOneToOne()) {
              throw new ClientError(i18n.message
              (Fields_contains_invalid_item, AbstractRequest.FIELDS, f, fn));
            }
            long roleId = isUpdate ? cd.updateRoleId : cd.roleId;
            if(isAllowed(roleId, groups)) {
              if(isLast && expanded != null) {
                expanded.add(prefix);
              }
            } else {
              denied.add(prefix);
              break;
            }
          } else {
            if(p == parts.length - 1) {
              if(expanded != null) {
                expandLastRelation
                (expanded, denied, cd.clazz, prefix, groups, isUpdate);
              } else {
                throw new ClientError(i18n.message
                (Fields_contains_not_plain_column, AbstractRequest.FIELDS, f));
              }
            }
          }
          parentClass = cd.clazz;
        }
      }
    }
  }

  private List<String> readGroupNames
  (Collection<Integer> groupIds, EntityManager em) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<String> cq = cb.createQuery(String.class);
    Root<UserGroup> root = cq.from(UserGroup.class);
    cq.select(cb.concat(Constants.ROLE_PREFIX, root.get(UserGroup_.NAME)));
    cq.where(root.get(UserGroup_.ID).in(groupIds));
    return em.createQuery(cq).getResultList();
  }

  private final static String TO_SQL_HEAD = String.join(
    ","
  , Role_.ID
  , Role_.DEFAULT_ACCESS
  , Role_.IS_URL
  , Role_.UPDATE_FOR
  , Role_.ORPHAN
  , Role_.DENIED_FOR_ALL
  , Role_.NAME
  , Role_.OBJ_NAME
  , Role_.DESCRIPTION
  );

  private static String toSqlInsert(Role role) {
    return "\n,(" + String.join(","
    , role.getId().toString()
    , Byte.toString(role.getDefaultAccess().id)
    , role.getIsUrl()? "1" : "0"
    , role.getUpdateFor() ? "1" : "0"
    , role.getOrphan() ? "1" : "0"
    , role.getDeniedForAll() ? "1" : "0"
    , "'" + role.getName() + "'"
    , "'" + role.getObjName() + "'"
    , "'" + role.getDescription() + "'"
    ) + ")";
  }

  private void updateCheck(
    Object data
  , Class<?> dataClass
  , Set<String> denied
  , String prefix
  , Collection<String> groups
  ) {
    if(data != null) {
      try {
        for(ColumnDescr cd : ColumnDescr.getCds(dataClass)) {
          if(!Identifiable.ID.equals(cd.name) && cd.isPlainOrRelationKey()) {
            Object v = PropertyUtils.getProperty(data, cd.name);
            if(v != null) {
              String prefixedName = prefix;
              if(!prefixedName.isEmpty()) {
                prefixedName += ".";
              }
              prefixedName += cd.name;
              if(!isAllowed(cd.updateRoleId, groups)) {
                denied.add(prefixedName);
              } else if(cd.isOneToOne()) {
                updateCheck(v, cd.clazz, denied, prefixedName, groups);
              }
            }
          }
        }
      }
      catch(IllegalAccessException | InvocationTargetException
      | NoSuchMethodException ex) {
        throw new ClientError(ex);
      }
    }
  }

}
