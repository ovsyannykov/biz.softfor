package biz.softfor.vaadin.partner;

import biz.softfor.partner.api.PartnerFltr;
import biz.softfor.partner.jpa.Partner;
import biz.softfor.partner.jpa.Partner_;
import biz.softfor.partner.jpa.PersonDetails_;
import biz.softfor.util.StringUtil;
import biz.softfor.util.api.ReadRequest;
import biz.softfor.util.api.filter.Expr;
import biz.softfor.util.api.filter.Value;
import biz.softfor.util.partner.PartnerType;
import biz.softfor.vaadin.field.ManyToOneField;
import java.util.List;
import java.util.function.BiConsumer;

public class PartnerField extends ManyToOneField<Long, Partner, PartnerFltr> {

  public final static BiConsumer<ReadRequest<Long, PartnerFltr>, String>
  FILL_REQUEST = (request, lookingFor) -> {
    String like = "%" + lookingFor.toLowerCase() + "%";
    request.filter.and(new Expr(Expr.OR
    , new Expr(Expr.LIKE
      , new Expr(Expr.LOWER, Partner_.PARTNER_NAME)
      , new Value(like)
      )
    , new Expr(Expr.LIKE
      , new Expr(Expr.LOWER, Partner_.PARTNER_FULLNAME)
      , new Value(like)
      )
    , new Expr(Expr.AND
      , new Expr(Expr.IN
        , Partner_.TYP
        , PartnerType.PERSON
        , PartnerType.EMPLOYEE
        )
      , new Expr(Expr.LIKE
        , new Expr(Expr.LOWER
          , StringUtil.field
            (Partner_.PERSON_DETAILS, PersonDetails_.MIDDLENAME)
        )
        , new Value(like)
        )
      )
    ));
  };

  public PartnerField(String name, PartnersBasicDbGrid partners) {
    super(
      name
    , partners
    , Partner::label
    , Partner::details
    , List.of(
        Partner_.PARTNER_NAME
      , Partner_.PARTNER_FULLNAME
      , Partner_.TYP
      , StringUtil.field(Partner_.PERSON_DETAILS, PersonDetails_.MIDDLENAME)
      )
    , FILL_REQUEST
    );
  }

}
