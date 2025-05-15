package biz.softfor.address.spring.rest;

import biz.softfor.address.jpa.CountryRequest;
import biz.softfor.address.jpa.CountryWor;
import biz.softfor.address.spring.CountrySvc;
import biz.softfor.jpa.crud.AbstractCrudSvc;
import biz.softfor.spring.security.SecurityUtil;
import biz.softfor.spring.security.service.JsonFilters;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.util.Create;
import biz.softfor.util.api.CommonResponse;
import biz.softfor.util.api.StdPath;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = StdPath.ROOT + "country/", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class CountryCtlr {

  private final JsonFilters jsonFilters;
  private final SecurityMgr securityMgr;
  private final CountrySvc service;

  @RequestMapping(path = StdPath.CREATE, method = RequestMethod.POST)
  public CommonResponse<CountryWor> create
  (@RequestBody @Validated(Create.class) CountryRequest.Create request) {
    securityMgr.methodCheck
    (service.serviceClass(), AbstractCrudSvc.CREATE_METHOD, SecurityUtil.groups());
    return service.create(request);
  }

  @RequestMapping(path = StdPath.READ, method = RequestMethod.POST)
  public MappingJacksonValue read(@RequestBody CountryRequest.Read request) {
    securityMgr.readCheck(service, request, SecurityUtil.groups());
    return jsonFilters.filter(service::read, request, service.clazz());
  }

  @RequestMapping(path = StdPath.UPDATE, method = RequestMethod.POST)
  public CommonResponse update(@RequestBody CountryRequest.Update request) {
    securityMgr.updateCheck(service, request, SecurityUtil.groups());
    service.validateUpdate(request);
    return service.update(request);
  }

  @RequestMapping(path = StdPath.DELETE, method = RequestMethod.POST)
  public CommonResponse delete(@RequestBody CountryRequest.Delete request) {
    securityMgr.methodCheck
    (service.serviceClass(), AbstractCrudSvc.DELETE_METHOD, SecurityUtil.groups());
    return service.delete(request);
  }

}
