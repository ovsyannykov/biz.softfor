package biz.softfor.vaadin.address;

import biz.softfor.address.api.CityTypeFltr;
import biz.softfor.address.jpa.CityType;
import biz.softfor.address.jpa.CityType_;
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
public class CityTypeDbGridColumns extends DbGridColumns<Short, CityType> {

  public static List<Order> DEFAULT_SORT
  = List.of(new Order(Order.Direction.ASC, CityType_.NAME));

  public CityTypeDbGridColumns(SecurityMgr securityMgr) {
    super(
      CityType.TABLE
    , securityMgr
    , CityType.class
    , new TextDbGridColumn<>(CityType_.NAME, CityTypeFltr::setName)
    , new TextDbGridColumn<>(CityType_.FULLNAME, CityTypeFltr::setFullname)
    );
  }

  @Override
  public List<Order> sort() {
    return DEFAULT_SORT;
  }

}
