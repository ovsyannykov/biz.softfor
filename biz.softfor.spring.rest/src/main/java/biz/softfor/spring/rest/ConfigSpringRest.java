package biz.softfor.spring.rest;

import biz.softfor.spring.rest.cachedbodyrequestfilter.CachedBodyRequestFilter;
import biz.softfor.spring.rest.errorhandling.ErrorController;
import biz.softfor.spring.rest.ping.PingCtlr;
import biz.softfor.util.api.ErrorData;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackageClasses
= { CachedBodyRequestFilter.class, ErrorController.class, PingCtlr.class })
@PropertySource("classpath:/biz.softfor.spring.rest.properties")
public class ConfigSpringRest {

  @Value("${biz.softfor.spring.rest.errorhandling.secretKeys}")
  private String[] secretKeys;

  @PostConstruct
  public void postConstruct() {
    ErrorData.setSecretKeys(secretKeys);
  }

}
