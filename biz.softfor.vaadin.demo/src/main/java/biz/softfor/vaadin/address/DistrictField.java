package biz.softfor.vaadin.address;

import biz.softfor.address.jpa.District;
import biz.softfor.address.jpa.District_;
import biz.softfor.vaadin.field.ManyToOneField;
import java.util.List;

public class DistrictField extends ManyToOneField<Integer, District> {

  public DistrictField(String name, DistrictsDbGrid dbGrid) {
    super(
      name
    , dbGrid
    , District::getName
    , District::getFullname
    , List.of(District_.NAME)
    );
  }

}
