package biz.softfor.vaadin.partner;

import biz.softfor.partner.jpa.Partner;
import biz.softfor.partner.jpa.Partner_;
import biz.softfor.partner.jpa.PersonDetails_;
import biz.softfor.util.StringUtil;
import biz.softfor.vaadin.field.ManyToOneField;
import java.util.List;

public class PartnerField extends ManyToOneField<Long, Partner> {

  public PartnerField(String name, PartnersBasicDbGrid partners) {
    super(
      name
    , partners
    , Partner::label
    , Partner::details
    , List.of(
        Partner_.PARTNER_NAME
      , Partner_.PARTNER_FULLNAME
      , StringUtil.field(Partner_.PERSON_DETAILS, PersonDetails_.MIDDLENAME)
      )
    );
  }

}
