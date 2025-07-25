package biz.softfor.vaadin.address;

import biz.softfor.address.api.CityFltr;
import biz.softfor.address.api.CityTypeFltr;
import biz.softfor.address.api.DistrictFltr;
import biz.softfor.address.api.StateFltr;
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
import biz.softfor.util.api.ReadRequest;
import biz.softfor.util.api.filter.Expr;
import biz.softfor.util.api.filter.Value;
import biz.softfor.vaadin.dbgrid.DbGridColumns;
import biz.softfor.vaadin.dbgrid.ManyToOneDbGridColumn;
import biz.softfor.vaadin.dbgrid.TextDbGridColumn;
import biz.softfor.vaadin.VaadinUtil;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.util.List;
import java.util.function.BiConsumer;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CityDbGridColumns extends DbGridColumns<Integer, City> {

  public static List<Order> DEFAULT_SORT
  = List.of(new Order(Order.Direction.ASC, City_.NAME));

  public final static BiConsumer<ReadRequest<Integer, CityFltr>, String>
  FILL_REQUEST = (request, lookingFor) -> {
    String like = "%" + lookingFor.toLowerCase() + "%";
    request.filter.and(new Expr(Expr.LIKE
    , new Expr(Expr.LOWER, City_.NAME)
    , new Value(like)
    ));
  };

  public final static BiConsumer<ReadRequest<Short, CityTypeFltr>, String>
  CITY_TYPE_FILL_REQUEST = (request, lookingFor) -> {
    String like = "%" + lookingFor.toLowerCase() + "%";
    request.filter.and(new Expr(Expr.OR
    , new Expr(Expr.LIKE
      , new Expr(Expr.LOWER, CityType_.NAME)
      , new Value(like)
      )
    , new Expr(Expr.LIKE
      , new Expr(Expr.LOWER, CityType_.FULLNAME)
      , new Value(like)
      )
    ));
  };

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
    , new ManyToOneDbGridColumn<>(
        City_.TYP
      , VaadinUtil.defaultRenderer(m -> {
          CityType e = m.getTyp();
          return e == null ? "" : e.getName();
        })
      , ManyToOneDbGridColumn.defaultFilter
        (CityFltr::getTyp, CityFltr::setTyp, CityTypeFltr::new)
      , cityTypesDbGrid
      , CityType::getName
      , CityType::getName
      , List.of(CityType_.NAME, CityType_.FULLNAME)
      , CITY_TYPE_FILL_REQUEST
      )
    , new ManyToOneDbGridColumn<>(
        City_.STATE
      , VaadinUtil.defaultRenderer(m -> {
          State e = m.getState();
          return e == null ? "" : e.getName();
        })
      , ManyToOneDbGridColumn.defaultFilter
        (CityFltr::getState, CityFltr::setState, StateFltr::new)
      , statesDbGrid
      , State::getName
      , State::getFullname
      , List.of(State_.NAME, State_.FULLNAME)
      , StateField.FILL_REQUEST
      )
    , new ManyToOneDbGridColumn<>(
        City_.DISTRICT
      , VaadinUtil.defaultRenderer(m -> {
          District e = m.getDistrict();
          return e == null ? "" : e.getName();
        })
      , ManyToOneDbGridColumn.defaultFilter
        (CityFltr::getDistrict, CityFltr::setDistrict, DistrictFltr::new)
      , districtsDbGrid
      , District::getName
      , District::getFullname
      , List.of(District_.NAME, District_.FULLNAME)
      , DistrictField.FILL_REQUEST
      )
    );
  }

  @Override
  public List<Order> sort() {
    return DEFAULT_SORT;
  }

}
