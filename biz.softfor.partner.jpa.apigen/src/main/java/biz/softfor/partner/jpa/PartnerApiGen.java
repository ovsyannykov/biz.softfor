package biz.softfor.partner.jpa;

import biz.softfor.jpa.apigen.ApiGen;
import biz.softfor.partner.spring.rest.RestControllers;

public class PartnerApiGen extends ApiGen {

  public PartnerApiGen() {
    super(Entities.class, RestControllers.class);
  }

}
