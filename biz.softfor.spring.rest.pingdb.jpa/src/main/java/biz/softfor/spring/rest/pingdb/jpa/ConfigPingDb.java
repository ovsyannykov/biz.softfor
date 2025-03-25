package biz.softfor.spring.rest.pingdb.jpa;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan
@PropertySource("classpath:/biz.softfor.spring.jpa.properties")
public class ConfigPingDb {
}
