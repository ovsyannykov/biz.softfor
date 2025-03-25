package biz.softfor.spring.rest.pingdb.jpa;

import biz.softfor.util.api.CommonResponse;
import biz.softfor.util.api.StdPath;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingDbCtlr {

  @PersistenceContext
  private EntityManager em;

  @RequestMapping(path = StdPath.PINGDB, method = { RequestMethod.GET, RequestMethod.POST }, produces = MediaType.APPLICATION_JSON_VALUE)
  public CommonResponse pingdb() {
    em.createNativeQuery("SELECT 0").getSingleResult();
    return new CommonResponse();
  }

}
