package biz.softfor.user.jpa;

import biz.softfor.jpa.apigen.ApiGen;
import biz.softfor.user.spring.rest.RestControllers;

public class UserApiGen extends ApiGen {

  public UserApiGen() {
    super(Entities.class, RestControllers.class);
  }

}
