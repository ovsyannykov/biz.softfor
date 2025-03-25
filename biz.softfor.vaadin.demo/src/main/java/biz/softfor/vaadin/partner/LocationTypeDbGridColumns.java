package biz.softfor.vaadin.partner;

import biz.softfor.partner.api.LocationTypeFltr;
import biz.softfor.partner.jpa.LocationType;
import biz.softfor.partner.jpa.LocationType_;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.util.api.Order;
import biz.softfor.vaadin.dbgrid.DbGridColumns;
import biz.softfor.vaadin.dbgrid.TextDbGridColumn;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.util.List;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LocationTypeDbGridColumns extends DbGridColumns<Short, LocationType> {

  public static List<Order> DEFAULT_SORT
  = List.of(new Order(Order.Direction.ASC, LocationType_.NAME));

  public LocationTypeDbGridColumns(SecurityMgr securityMgr) {
    super(
      LocationType.TABLE
    , securityMgr
    , LocationType.class
    , new TextDbGridColumn<>(LocationType_.NAME, LocationTypeFltr::setName)
    , new TextDbGridColumn<>(LocationType_.DESCR, LocationTypeFltr::setDescr)
    );
  }

  @Override
  public List<Order> sort() {
    return DEFAULT_SORT;
  }

}
