package biz.softfor.vaadin.address;

import biz.softfor.address.api.CountryFltr;
import biz.softfor.address.jpa.Country;
import biz.softfor.address.jpa.Country_;
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
public class CountryDbGridColumns extends DbGridColumns<Short, Country> {

  public static List<Order> DEFAULT_SORT
  = List.of(new Order(Order.Direction.ASC, Country_.NAME));

  public CountryDbGridColumns(SecurityMgr securityMgr) {
    super(
      Country.TABLE
    , securityMgr
    , Country.class
    , new TextDbGridColumn<>(Country_.NAME, CountryFltr::setName)
    , new TextDbGridColumn<>(Country_.FULLNAME, CountryFltr::setFullname)
    //, new NumberDbGridColumn<Country, Short, ShortField, CountryFltr>
    //  (Country_.ID, ShortField.class, (f, v) -> f.assignId(v))
    );
  }

  @Override
  public List<Order> sort() {
    return DEFAULT_SORT;
  }

}
