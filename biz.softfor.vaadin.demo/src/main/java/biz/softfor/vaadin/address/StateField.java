package biz.softfor.vaadin.address;

import biz.softfor.address.jpa.State;
import biz.softfor.address.jpa.State_;
import biz.softfor.vaadin.field.ManyToOneField;
import java.util.List;

public class StateField extends ManyToOneField<Integer, State> {

  public StateField(String name, StatesDbGrid dbGrid) {
    super(
      name
    , dbGrid
    , State::getName
    , State::getFullname
    , List.of(State_.NAME, State_.FULLNAME)
    );
  }

}
