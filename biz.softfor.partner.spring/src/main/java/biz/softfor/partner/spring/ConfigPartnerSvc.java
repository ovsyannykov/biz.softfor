package biz.softfor.partner.spring;

import biz.softfor.address.jpa.Postcode;
import biz.softfor.address.jpa.PostcodeWor;
import biz.softfor.address.spring.PostcodeSvc;
import biz.softfor.partner.jpa.Partner;
import biz.softfor.partner.jpa.PartnerWor;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = { PartnerSvc.class, PostcodeSvc.class })
@EntityScan(basePackageClasses = {
  Partner.class, Postcode.class
, PartnerWor.class, PostcodeWor.class
})
public class ConfigPartnerSvc {
}
