package biz.softfor.user.spring.rest;

import biz.softfor.jpa.crud.AbstractCrudSvc;
import biz.softfor.spring.security.SecurityUtil;
import biz.softfor.spring.security.service.JsonFilters;
import biz.softfor.user.jpa.UserRequest;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.user.spring.UserSvc;
import biz.softfor.util.api.CommonResponse;
import biz.softfor.util.api.StdPath;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = StdPath.ROOT + "user/", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserCtlr {

  private final JsonFilters jsonFilters;
  private final SecurityMgr securityMgr;
  private final UserSvc service;

  @RequestMapping(path = StdPath.READ, method = RequestMethod.POST)
  public MappingJacksonValue read(@RequestBody UserRequest.Read request) {
    securityMgr.readCheck(service, request, SecurityUtil.groups());
    return jsonFilters.filter(service::read, request, service.clazz());
  }

  @RequestMapping(path = StdPath.UPDATE, method = RequestMethod.POST)
  public CommonResponse update(@RequestBody @Valid UserRequest.Update request) {
    securityMgr.updateCheck(service, request, SecurityUtil.groups());
    service.validateUpdate(request);
    return service.update(request);
  }

  @RequestMapping(path = StdPath.DELETE, method = RequestMethod.POST)
  public CommonResponse delete(@RequestBody UserRequest.Delete request) {
    securityMgr.deleteCheck(service, SecurityUtil.groups());
    return service.delete(request);
  }

}
