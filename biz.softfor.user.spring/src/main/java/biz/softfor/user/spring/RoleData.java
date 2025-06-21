package biz.softfor.user.spring;

import biz.softfor.util.security.DefaultAccess;
import java.text.MessageFormat;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.java.Log;
import org.apache.commons.collections4.CollectionUtils;

@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Log
public final class RoleData {

  @EqualsAndHashCode.Include public final long id;
  int version;
  public boolean deniedForAll;
  public final boolean updateFor;
  public final boolean isUrl;
  public final String objName;
  public final RoleAccess access;
  public RoleAccess effective;

  public RoleData(
    long id
  , boolean deniedForAll
  , boolean updateFor
  , boolean isUrl
  , DefaultAccess defaultAccess
  , String objName
  , Set<String> groups
  ) {
    this.id = id;
    version = Integer.MIN_VALUE;
    this.deniedForAll = deniedForAll;
    this.updateFor = updateFor;
    this.isUrl = isUrl;
    this.objName = objName;
    this.access = new RoleAccess(defaultAccess, groups);
    this.effective = new RoleAccess();
  }

  String toStr() {
    return (updateFor ? "~" + objName : objName) + " (" + id + ")";
  }

  private void mergeTypeData(
    SecurityMgr securityMgr
  , Deque<RoleData> parents
  , ParentRoles parentRoles
  , boolean reset
  ) {
    Long typeId = parentRoles.type();
    if(typeId != null) {//User#groups
      RoleData typeData = securityMgr.rolesData.get(typeId);
      if(!parents.contains(typeData)) {
        parents.push(typeData);
        typeData.recount(securityMgr, parents);
        parents.pop();
        if(reset) {
          reset();
        }
        merge(typeData.effective);
      }
    }
  }

  void recount(SecurityMgr securityMgr, Deque<RoleData> parents) {
    if(version < securityMgr.version) {
      if(SecurityMgr.DEBUG) System.out.println("effRecount access " + toStr() + ": " + access.defaultAccess.name() + " " + access.groups);
      if(securityMgr.parent2Members.get(id) == null) {//User#email, User#groups
        ParentRoles parentRoles = securityMgr.member2Parent.get(id);
        RoleData hostData = securityMgr.rolesData.get(parentRoles.host());
        reset();
        mergeTypeData(securityMgr, parents, parentRoles, false);
        merge(hostData.access);
      } else {//User
        RoleAccess membersAccess
        = new RoleAccess(DefaultAccess.NOBODY, new HashSet<>());
        boolean isGroupsCleared = false;
        for(Long memberId : securityMgr.parent2Members.get(id)) {
          RoleData memberData = securityMgr.rolesData.get(memberId);
          ParentRoles parentRoles = securityMgr.member2Parent.get(memberData.id);
          memberData.mergeTypeData(securityMgr, parents, parentRoles, true);
          if(membersAccess.defaultAccess.id > memberData.effective.defaultAccess.id) {
            membersAccess.defaultAccess = memberData.effective.defaultAccess;
          }
          if(!isGroupsCleared) {
            if(memberData.effective.groups.isEmpty()) {
              membersAccess.groups.clear();
              isGroupsCleared = true;
            } else {
              membersAccess.groups.addAll(memberData.effective.groups);
            }
          }
        }
        reset();
        merge(membersAccess);
      }
      if(SecurityMgr.DEBUG) System.out.println("effRecount effective " + toStr() + ": " + effective.defaultAccess.name() + " " + effective.groups);
      ++version;
    }
  }

  private void merge(RoleAccess parent) {
    if(CollectionUtils.isEmpty(effective.groups)) {
      switch(effective.defaultAccess) {
        case DefaultAccess.EVERYBODY -> {
          effective.defaultAccess = parent.defaultAccess;
          effective.groups.addAll(parent.groups);
        }
        case DefaultAccess.AUTHORIZED -> {
          if(parent.defaultAccess == DefaultAccess.NOBODY) {
            effective.defaultAccess = DefaultAccess.NOBODY;
          }
          effective.groups.addAll(parent.groups);
        }
        case DefaultAccess.NOBODY -> {}
      }
    } else {
      if(!parent.groups.isEmpty()) {
        effective.groups.retainAll(parent.groups);
      }
      if(effective.groups.isEmpty()) {
        effective.defaultAccess = DefaultAccess.NOBODY;
        log.warning(MessageFormat.format("Role is inaccessible: {0}", toStr()));
      }
    }
  }

  void reset() {
    effective.defaultAccess = access.defaultAccess;
    effective.groups.clear();
    effective.groups.addAll(access.groups);
  }

}
