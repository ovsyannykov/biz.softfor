package biz.softfor.user.spring;

import biz.softfor.util.security.DefaultAccess;
import static biz.softfor.util.security.DefaultAccess.AUTHORIZED;
import static biz.softfor.util.security.DefaultAccess.EVERYBODY;
import static biz.softfor.util.security.DefaultAccess.NOBODY;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.java.Log;
import org.apache.commons.collections4.CollectionUtils;

@ToString
@EqualsAndHashCode
@Log
public final class RoleData {

  public final static RoleData EMPTY = new RoleData(0, false, true, false, false
  , DefaultAccess.EVERYBODY, "EMPTY", Collections.EMPTY_SET);

  public final long id;
  public boolean deniedForAll;
  public boolean disabled;
  public final boolean updateFor;
  public final boolean isUrl;
  public final String objName;
  @EqualsAndHashCode.Exclude public DefaultAccess defaultAccess;
  @EqualsAndHashCode.Exclude public final Set<String> groups;
  @EqualsAndHashCode.Exclude public DefaultAccess effDefaultAccess;
  @EqualsAndHashCode.Exclude public final Set<String> effGroups;

  public RoleData(
    long id
  , boolean deniedForAll
  , boolean disabled
  , boolean updateFor
  , boolean isUrl
  , DefaultAccess defaultAccess
  , String objName
  , Set<String> groups
  ) {
    this.id = id;
    this.deniedForAll = deniedForAll;
    this.disabled = disabled;
    this.updateFor = updateFor;
    this.isUrl = isUrl;
    this.objName = objName;
    this.defaultAccess = defaultAccess;
    this.groups = groups;
    this.effDefaultAccess = this.defaultAccess;
    this.effGroups = new HashSet<>(this.groups);
  }

  public void calcEffectiveAccess(SecurityMgr securityMgr) {
    List<Long> members = securityMgr.parent2Members.get(id);
    if(members == null) {
      ParentRoles parentRoles = securityMgr.member2Parent.get(id);
      effReset();
      RoleData host = securityMgr.rolesData.get(parentRoles.host());
      effCalcFromParent(host, securityMgr);
      if(parentRoles.type() != null) {
        RoleData type = securityMgr.rolesData.get(parentRoles.type());
        effCalcFromParent(type, securityMgr);
      }
    } else {
      for(Long m : members) {
        RoleData mrd = securityMgr.rolesData.get(m);
        mrd.effReset();
        mrd.effCalcFromParent(this, securityMgr);
      }
    }
  }

  public void effCalcFromParent(RoleData parent, SecurityMgr securityMgr) {
    if(!parent.disabled) {
      if(CollectionUtils.isEmpty(effGroups)) {
        switch(defaultAccess) {
          case EVERYBODY -> {
            effDefaultAccess = parent.defaultAccess;
            effGroups.addAll(parent.groups);
          }
          case AUTHORIZED -> {
            if(parent.defaultAccess == DefaultAccess.NOBODY) {
              effDefaultAccess = DefaultAccess.NOBODY;
            }
            effGroups.addAll(parent.groups);
          }
          case NOBODY -> {}
        }
      } else {
        for(Iterator<String> i = effGroups.iterator(); i.hasNext();) {
          if(!securityMgr.isAllowed(parent.id, List.of(i.next()))) {
            i.remove();
          }
        }
        if(effGroups.isEmpty()) {
          effDefaultAccess = DefaultAccess.NOBODY;
          log.warning(() -> MessageFormat.format
          ("Role is inaccessible: {0}", toString()));
        }
      }
    }
  }

  public void effReset() {
    effDefaultAccess = defaultAccess;
    effGroups.clear();
    effGroups.addAll(groups);
  }

  public String toStr() {
    return id + " (" + (updateFor ? "~" + objName : objName) + ")";
  }

}
