package biz.softfor.user.spring;

import biz.softfor.user.jpa.Role;
import java.util.List;
import java.util.Map;

public interface ScanRoles {

  public void load(
    Map<Long, Role> fromCode
  , Map<Long, ParentRoles> member2Parent
  , Map<Long, List<Long>> parent2Members
  );

}
