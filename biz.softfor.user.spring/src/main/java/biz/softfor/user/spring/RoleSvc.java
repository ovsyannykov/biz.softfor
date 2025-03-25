package biz.softfor.user.spring;

import biz.softfor.jpa.crud.AbstractCrudSvc;
import biz.softfor.spring.jpa.crud.CrudSvc;
import biz.softfor.user.api.RoleFltr;
import biz.softfor.user.jpa.Role;
import biz.softfor.user.jpa.RoleRequest;
import biz.softfor.user.jpa.RoleWor;
import biz.softfor.user.jpa.Role_;
import biz.softfor.util.Constants;
import biz.softfor.util.api.ClientError;
import biz.softfor.util.api.CommonResponse;
import biz.softfor.util.api.CreateRequest;
import biz.softfor.util.api.DeleteRequest;
import biz.softfor.util.api.Identifiable;
import biz.softfor.util.api.UpdateRequest;
import biz.softfor.util.api.filter.Expr;
import biz.softfor.util.api.filter.Value;
import biz.softfor.util.security.ActionAccess;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleSvc
extends CrudSvc<Long, Role, RoleWor, RoleFltr> {

  private final static Expr DELETE_CONSTRAINT
  = new Expr(Expr.EQUAL, Role_.ORPHAN, new Value(Boolean.TRUE));

  private final SecurityMgr securityMgr;

  @ActionAccess(deniedForAll = true)
  @Override
  public CommonResponse<RoleWor> create(CreateRequest request) {
    throw new ClientError(i18n.message
    (Constants.Unsupported_operation, AbstractCrudSvc.CREATE_METHOD));
  }

  @Override
  public CommonResponse delete(DeleteRequest request) {
    request.filter.andAnd(DELETE_CONSTRAINT);
    CommonResponse result = super.delete(request);
    if(result.isOk() && result.getTotal() == 0) {
      result.setDescr
      (i18n.message(Role.Delete_constraint, result.getDescr()));
    }
    return result;
  }

  @Override
  public CommonResponse update(UpdateRequest request) {
    RoleRequest.Read req = new RoleRequest.Read();
    req.filter = (RoleFltr)request.filter;
    req.fields = List.of(Role_.ID);
    CommonResponse<Role> res = read(req);
    Set<Long> ids = Identifiable.ids(res.getData());
    request.filter = new RoleFltr();
    request.filter.setId(ids);
    CommonResponse result = super.update(request);
    securityMgr.update(ids, (RoleWor)request.data, em);
    return result;
  }

}
