package biz.softfor.testutil.spring;

import biz.softfor.testutil.Check;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class ConfigTestUtil {

  @Bean
  public Check check(ObjectMapper objectMapper) {
    return new Check(objectMapper);
  }

}
