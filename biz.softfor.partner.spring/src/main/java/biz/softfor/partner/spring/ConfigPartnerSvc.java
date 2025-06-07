package biz.softfor.partner.spring;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = { biz.softfor.partner.spring.PartnerSvc.class, biz.softfor.address.spring.PostcodeSvc.class })
@EntityScan(basePackageClasses = { biz.softfor.partner.jpa.Partner.class, biz.softfor.address.jpa.Postcode.class })
public class ConfigPartnerSvc {
}
