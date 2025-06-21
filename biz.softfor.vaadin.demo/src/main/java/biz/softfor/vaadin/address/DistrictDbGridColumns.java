package biz.softfor.vaadin.address;

import biz.softfor.address.api.DistrictFltr;
import biz.softfor.address.api.StateFltr;
import biz.softfor.address.jpa.District;
import biz.softfor.address.jpa.District_;
import biz.softfor.address.jpa.State;
import biz.softfor.address.jpa.State_;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.util.api.Order;
import biz.softfor.vaadin.dbgrid.DbGridColumns;
import biz.softfor.vaadin.dbgrid.ManyToOneDbGridColumn;
import biz.softfor.vaadin.dbgrid.TextDbGridColumn;
import biz.softfor.vaadin.VaadinUtil;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.util.List;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DistrictDbGridColumns extends DbGridColumns<Integer, District> {

  public static List<Order> DEFAULT_SORT
  = List.of(new Order(Order.Direction.ASC, District_.NAME));

  public DistrictDbGridColumns
  (SecurityMgr securityMgr, StatesDbGrid statesDbGrid) {
    super(
      District.TABLE
    , securityMgr
    , District.class
    , new TextDbGridColumn<>(District_.NAME, DistrictFltr::setName)
    , new TextDbGridColumn<>(District_.FULLNAME, DistrictFltr::setFullname)
    , new ManyToOneDbGridColumn<>(
        District_.STATE
      , VaadinUtil.defaultRenderer(m -> {
          State e = m.getState();
          return e == null ? "" : e.getName();
        })
      , ManyToOneDbGridColumn.defaultFilter
        (DistrictFltr::getState, DistrictFltr::setState, StateFltr::new)
      , statesDbGrid
      , State::getName
      , State::getFullname
      , List.of(State_.NAME, State_.FULLNAME)
      )
    );
  }

  @Override
  public List<Order> sort() {
    return DEFAULT_SORT;
  }

}
