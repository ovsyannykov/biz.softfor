package biz.softfor.vaadin.address;

import biz.softfor.address.api.CityFltr;
import biz.softfor.address.api.DistrictFltr;
import biz.softfor.address.api.PostcodeFltr;
import biz.softfor.address.jpa.City;
import biz.softfor.address.jpa.City_;
import biz.softfor.address.jpa.District;
import biz.softfor.address.jpa.District_;
import biz.softfor.address.jpa.Postcode;
import biz.softfor.address.jpa.Postcode_;
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
public class PostcodeDbGridColumns extends DbGridColumns<Integer, Postcode> {

  public static List<Order> DEFAULT_SORT
  = List.of(new Order(Order.Direction.ASC, Postcode_.POSTCODE));

  public PostcodeDbGridColumns(
    SecurityMgr securityMgr
  , DistrictsDbGrid districtsDbGrid
  , CitiesDbGrid citiesDbGrid
  ) {
    super(
      Postcode.TABLE
    , securityMgr
    , Postcode.class
    , new TextDbGridColumn<>(Postcode_.POSTCODE, PostcodeFltr::setPostcode)
    , new ManyToOneDbGridColumn<Postcode, PostcodeFltr, Integer, District>(
        Postcode_.DISTRICT
      , VaadinUtil.defaultRenderer(m -> {
          District e = m.getDistrict();
          return e == null ? "" : e.getName();
        })
      , defaultFilter
        (PostcodeFltr::getDistrict, PostcodeFltr::setDistrict, DistrictFltr::new)
      , districtsDbGrid
      , District::getName
      , District::getFullname
      , List.of(District_.NAME, District_.FULLNAME)
      )
    , new ManyToOneDbGridColumn<Postcode, PostcodeFltr, Integer, City>(
        Postcode_.CITY
      , VaadinUtil.defaultRenderer(m -> {
          City e = m.getCity();
          return e == null ? "" : e.getName();
        })
      , defaultFilter
        (PostcodeFltr::getCity, PostcodeFltr::setCity, CityFltr::new)
      , citiesDbGrid
      , City::getName
      , City::getName
      , List.of(City_.NAME)
      )
    );
  }

  @Override
  public List<Order> sort() {
    return DEFAULT_SORT;
  }

}
