package biz.softfor.i18nspring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan
@PropertySource("classpath:/biz.softfor.spring.i18n.properties")
public class ConfigI18nSpring {
}
