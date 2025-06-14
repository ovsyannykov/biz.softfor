package biz.softfor.user.spring;

import biz.softfor.user.jpa.User;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@EntityScan(basePackageClasses = { User.class })
public class ConfigUserSpring {}
