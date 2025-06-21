package biz.softfor.user.spring.rest.testassets;

import biz.softfor.spring.security.SecurityUtil;
import biz.softfor.spring.security.service.JsonFilters;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.util.Create;
import biz.softfor.util.api.CommonResponse;
import biz.softfor.util.api.StdPath;
import biz.softfor.util.security.ActionAccess;
import biz.softfor.util.security.DefaultAccess;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = TeztEntityRequest.TEST_SECURITY, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class DefaultAccessCtlr {

  private final JsonFilters jsonFilters;
  private final SecurityMgr securityMgr;
  private final TeztEntitySvc service;

  @RequestMapping(path = StdPath.CREATE, method = RequestMethod.POST)
  public CommonResponse<TeztEntityWor> create
  (@RequestBody @Validated(Create.class) TeztEntityRequest.Create request) {
    securityMgr.createCheck(service, SecurityUtil.groups());
    return service.create(request);
  }

  @RequestMapping(path = StdPath.READ, method = RequestMethod.POST)
  public MappingJacksonValue read(@RequestBody TeztEntityRequest.Read request) {
    securityMgr.readCheck(service, request, SecurityUtil.groups());
    return jsonFilters.filter(service::read, request, service.clazz());
  }

  @RequestMapping(path = TeztEntityRequest.DATA_DEFAULT_ACCESS, method = { RequestMethod.POST })
  public MappingJacksonValue dataDefaultAccess
  (@RequestBody TeztEntityRequest.Read request) {
    securityMgr.readCheck(service, request, SecurityUtil.groups());
    return jsonFilters.filter(service::read, request, service.clazz());
  }

  @RequestMapping(path = TeztEntityRequest.DATA_DEFAULT_UPDATE_ACCESS, method = { RequestMethod.POST })
  public CommonResponse dataDefaultUpdateAccess
  (@RequestBody TeztEntityRequest.Update request) {
    securityMgr.updateCheck(service, request, SecurityUtil.groups());
    return service.update(request);
  }

  @RequestMapping(path = TeztEntityRequest.URL_DEFAULT_ACCESS_EVERYBODY, method = { RequestMethod.POST })
  @ActionAccess(defaultAccess = DefaultAccess.EVERYBODY)
  public CommonResponse urlDefaultAccessEverybody() {
    return new CommonResponse();
  }

  @RequestMapping(path = TeztEntityRequest.URL_DEFAULT_ACCESS_AUTHORIZED, method = { RequestMethod.POST })
  @ActionAccess(defaultAccess = DefaultAccess.AUTHORIZED)
  public CommonResponse urlDefaultAccessAuthorized() {
    return new CommonResponse();
  }

  @RequestMapping(path = TeztEntityRequest.URL_DEFAULT_ACCESS_NOBODY, method = { RequestMethod.POST })
  @ActionAccess(defaultAccess = DefaultAccess.NOBODY)
  public CommonResponse urlDefaultAccessNobody() {
    return new CommonResponse();
  }

}
