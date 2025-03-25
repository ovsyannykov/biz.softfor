package biz.softfor.user.spring;

import biz.softfor.spring.jpa.crud.CrudSvc;
import biz.softfor.user.api.UserGroupFltr;
import biz.softfor.user.jpa.UserGroup;
import biz.softfor.user.jpa.UserGroupRequest;
import biz.softfor.user.jpa.UserGroupWor;
import biz.softfor.user.jpa.UserGroup_;
import biz.softfor.util.api.CommonResponse;
import biz.softfor.util.api.CreateRequest;
import biz.softfor.util.api.DeleteRequest;
import biz.softfor.util.api.Identifiable;
import biz.softfor.util.api.UpdateRequest;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserGroupSvc
extends CrudSvc<Integer, UserGroup, UserGroupWor, UserGroupFltr> {

  private final SecurityMgr securityMgr;

  @Override
  public CommonResponse<UserGroupWor> create(CreateRequest request) {
    CommonResponse<UserGroupWor> result = super.create(request);
    Set<Long> roleIds = ((UserGroupWor)request.data).getRoleIds();
    if(CollectionUtils.isNotEmpty(roleIds)) {
      securityMgr.addGroup(result.getData(0).getId(), roleIds, em);
    }
    return result;
  }

  @Override
  public CommonResponse delete(DeleteRequest request) {
    UserGroupRequest.Read req = new UserGroupRequest.Read();
    req.filter = (UserGroupFltr)request.filter;
    req.fields = List.of(UserGroup_.ID);
    CommonResponse<UserGroup> res = read(req);
    Set<Integer> ids = Identifiable.ids(res.getData());
    request.filter = new UserGroupFltr();
    request.filter.setId(ids);
    CommonResponse result;
    if(CollectionUtils.isEmpty(ids)) {
      result = new CommonResponse();
    } else {
      result = super.delete(request);
      securityMgr.removeGroups(ids, em);
    }
    return result;
  }

  @Override
  public CommonResponse update(UpdateRequest request) {
    CommonResponse result;
    Set<Long> roleIds = null;
    if(request.data != null) {
      roleIds = ((UserGroupRequest.Update)request).data.getRoleIds();
    }
    if(roleIds == null) {
      result = super.update(request);
    } else {
      UserGroupRequest.Read req = new UserGroupRequest.Read();
      req.filter = (UserGroupFltr)request.filter;
      req.fields = List.of(UserGroup_.ID);
      CommonResponse<UserGroup> res = read(req);
      Set<Integer> groupIds = Identifiable.ids(res.getData());
      if(CollectionUtils.isEmpty(groupIds)) {
        result = new CommonResponse();
      } else {
        request.filter = new UserGroupFltr();
        request.filter.setId(groupIds);
        result = super.update(request);
        securityMgr.updateGroups(groupIds, roleIds, em);
      }
    }
    return result;
  }

}
