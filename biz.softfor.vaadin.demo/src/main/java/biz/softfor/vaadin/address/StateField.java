package biz.softfor.vaadin.address;

import biz.softfor.address.api.StateFltr;
import biz.softfor.address.jpa.State;
import biz.softfor.address.jpa.State_;
import biz.softfor.util.api.ReadRequest;
import biz.softfor.util.api.filter.Expr;
import biz.softfor.util.api.filter.Value;
import biz.softfor.vaadin.field.ManyToOneField;
import java.util.List;
import java.util.function.BiConsumer;

public class StateField extends ManyToOneField<Integer, State, StateFltr> {

  public final static BiConsumer<ReadRequest<Integer, StateFltr>, String>
  FILL_REQUEST = (request, lookingFor) -> {
    String like = "%" + lookingFor.toLowerCase() + "%";
    request.filter.and(new Expr(Expr.OR
    , new Expr(Expr.LIKE
      , new Expr(Expr.LOWER, State_.NAME)
      , new Value(like)
      )
    , new Expr(Expr.LIKE
      , new Expr(Expr.LOWER, State_.FULLNAME)
      , new Value(like)
      )
    ));
  };

  public StateField(String name, StatesDbGrid dbGrid) {
    super(
      name
    , dbGrid
    , State::getName
    , State::getFullname
    , List.of(State_.NAME, State_.FULLNAME)
    , FILL_REQUEST
    );
  }

}
