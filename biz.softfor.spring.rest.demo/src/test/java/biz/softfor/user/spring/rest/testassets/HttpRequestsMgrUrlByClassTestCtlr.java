package biz.softfor.user.spring.rest.testassets;

import biz.softfor.spring.security.SecurityUtil;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.util.api.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = TeztEntityRequest.TEST_SECURITY, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class HttpRequestsMgrUrlByClassTestCtlr {

  public final static String URL_BY_CLASS = "urlByClass";

  private final SecurityMgr securityMgr;

  @RequestMapping(path = URL_BY_CLASS, method = RequestMethod.GET)
  public CommonResponse urlByClass() {
    securityMgr.methodCheck(getClass(), URL_BY_CLASS, SecurityUtil.groups());
    return new CommonResponse();
  }

}
