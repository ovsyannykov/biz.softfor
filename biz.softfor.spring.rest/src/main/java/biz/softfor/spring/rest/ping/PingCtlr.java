package biz.softfor.spring.rest.ping;

import biz.softfor.util.api.CommonResponse;
import biz.softfor.util.api.StdPath;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingCtlr {

  @RequestMapping(path = StdPath.PING, method = { RequestMethod.GET, RequestMethod.POST }, produces = MediaType.APPLICATION_JSON_VALUE)
  public CommonResponse ping() {
    return new CommonResponse();
  }

}
