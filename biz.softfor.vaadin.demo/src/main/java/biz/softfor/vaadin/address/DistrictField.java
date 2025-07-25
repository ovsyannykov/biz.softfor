package biz.softfor.vaadin.address;

import biz.softfor.address.api.DistrictFltr;
import biz.softfor.address.jpa.District;
import biz.softfor.address.jpa.District_;
import biz.softfor.util.api.ReadRequest;
import biz.softfor.util.api.filter.Expr;
import biz.softfor.util.api.filter.Value;
import biz.softfor.vaadin.field.ManyToOneField;
import java.util.List;
import java.util.function.BiConsumer;

public class DistrictField
extends ManyToOneField<Integer, District, DistrictFltr> {

  public final static BiConsumer<ReadRequest<Integer, DistrictFltr>, String>
  FILL_REQUEST = (request, lookingFor) -> {
    String like = "%" + lookingFor.toLowerCase() + "%";
    request.filter.and(new Expr(Expr.OR
    , new Expr(Expr.LIKE
      , new Expr(Expr.LOWER, District_.NAME)
      , new Value(like)
      )
    , new Expr(Expr.LIKE
      , new Expr(Expr.LOWER, District_.FULLNAME)
      , new Value(like)
      )
    ));
  };

  public DistrictField(String name, DistrictsDbGrid dbGrid) {
    super(
      name
    , dbGrid
    , District::getName
    , District::getFullname
    , List.of(District_.NAME, District_.FULLNAME)
    , FILL_REQUEST
    );
  }

}
