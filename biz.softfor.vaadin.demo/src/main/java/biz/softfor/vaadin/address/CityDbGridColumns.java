package biz.softfor.vaadin.address;

import biz.softfor.address.api.CityFltr;
import biz.softfor.address.jpa.City;
import biz.softfor.address.jpa.CityType;
import biz.softfor.address.jpa.CityType_;
import biz.softfor.address.jpa.City_;
import biz.softfor.address.jpa.District;
import biz.softfor.address.jpa.District_;
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
public class CityDbGridColumns extends DbGridColumns<Integer, City> {

  public static List<Order> DEFAULT_SORT
  = List.of(new Order(Order.Direction.ASC, City_.NAME));

  public CityDbGridColumns(
    SecurityMgr securityMgr
  , CityTypesDbGrid cityTypesDbGrid
  , StatesDbGrid statesDbGrid
  , DistrictsDbGrid districtsDbGrid
  ) {
    super(
      City.TABLE
    , securityMgr
    , City.class
    , new TextDbGridColumn<>(City_.NAME, CityFltr::setName)
    , new ManyToOneDbGridColumn<City, CityFltr, Short, CityType>(
        City_.TYP
      , VaadinUtil.defaultRenderer(m -> {
          CityType e = m.getTyp();
          return e == null ? "" : e.getName();
        })
      , defaultFilter(CityFltr::setTypeId)
      , cityTypesDbGrid
      , CityType::getName
      , CityType::getName
      , List.of(CityType_.NAME)
      )
    , new ManyToOneDbGridColumn<City, CityFltr, Integer, State>(
        City_.STATE
      , VaadinUtil.defaultRenderer(m -> {
          State e = m.getState();
          return e == null ? "" : e.getName();
        })
      , defaultFilter(CityFltr::setStateId)
      , statesDbGrid
      , State::getName
      , State::getFullname
      , List.of(State_.NAME, State_.FULLNAME)
      )
    , new ManyToOneDbGridColumn<City, CityFltr, Integer, District>(
        City_.DISTRICT
      , VaadinUtil.defaultRenderer(m -> {
          District e = m.getDistrict();
          return e == null ? "" : e.getName();
        })
      , defaultFilter(CityFltr::setDistrictId)
      , districtsDbGrid
      , District::getName
      , District::getFullname
      , List.of(District_.NAME, District_.FULLNAME)
      )
    );
  }

  @Override
  public List<Order> sort() {
    return DEFAULT_SORT;
  }

}
