package biz.softfor.partner.spring.rest;

import biz.softfor.address.spring.rest.PostcodeCtlr;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = { PartnerCtlr.class, PostcodeCtlr.class })
public class ConfigPartnerRest {
}
