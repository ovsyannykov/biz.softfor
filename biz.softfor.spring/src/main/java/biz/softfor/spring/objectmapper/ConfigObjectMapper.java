package biz.softfor.spring.objectmapper;

import biz.softfor.util.Json;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan
@PropertySource("classpath:/biz.softfor.spring.objectmapper.properties")
public class ConfigObjectMapper {

  @Bean
  public ObjectMapper objectMapper() {
    return Json.objectMapper();
  }

}
