package biz.softfor.vaadin.address;

import biz.softfor.address.api.CountryFltr;
import biz.softfor.address.api.StateFltr;
import biz.softfor.address.jpa.Country;
import biz.softfor.address.jpa.Country_;
import biz.softfor.address.jpa.State;
import biz.softfor.address.jpa.State_;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.util.api.Order;
import biz.softfor.vaadin.dbgrid.DbGridColumns;
import biz.softfor.vaadin.dbgrid.ManyToOneDbGridColumn;
import static biz.softfor.vaadin.dbgrid.ManyToOneDbGridColumn.defaultFilter;
import biz.softfor.vaadin.dbgrid.TextDbGridColumn;
import biz.softfor.vaadin.VaadinUtil;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.util.List;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class StateDbGridColumns extends DbGridColumns<Integer, State> {

  public static List<Order> DEFAULT_SORT
  = List.of(new Order(Order.Direction.ASC, State_.NAME));

  public StateDbGridColumns
  (SecurityMgr securityMgr, CountriesDbGrid countriesDbGrid) {
    super(
      State.TABLE
    , securityMgr
    , State.class
    , new TextDbGridColumn<>(State_.NAME, StateFltr::setName)
    , new TextDbGridColumn<>(State_.FULLNAME, StateFltr::setFullname)
    , new ManyToOneDbGridColumn<State, StateFltr, Short, Country>(
        State_.COUNTRY
      , VaadinUtil.defaultRenderer(m -> {
          Country e = m.getCountry();
          return e == null ? "" : e.getName();
        })
      , defaultFilter
        (StateFltr::getCountry, StateFltr::setCountry, CountryFltr::new)
      , countriesDbGrid
      , Country::getName
      , Country::getFullname
      , List.of(Country_.NAME, Country_.FULLNAME)
      )
    );
  }

  @Override
  public List<Order> sort() {
    return DEFAULT_SORT;
  }

}
