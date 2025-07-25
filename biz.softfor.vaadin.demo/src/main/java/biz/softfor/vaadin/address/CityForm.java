package biz.softfor.vaadin.address;

import biz.softfor.address.jpa.City;
import biz.softfor.address.jpa.CityType;
import biz.softfor.address.jpa.CityType_;
import biz.softfor.address.jpa.CityWor;
import biz.softfor.address.jpa.City_;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.vaadin.EntityForm;
import biz.softfor.vaadin.EntityFormColumns;
import biz.softfor.vaadin.field.ManyToOneField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import jakarta.validation.Validator;
import java.util.LinkedHashMap;
import java.util.List;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CityForm extends EntityForm<Integer, City, CityWor> {

  public CityForm(
    SecurityMgr securityMgr
  , Validator validator
  , CityTypesDbGrid typesDbGrid
  , StatesDbGrid statesDbGrid
  , DistrictsDbGrid districtsDbGrid
  ) {
    super(City.TITLE
    , new EntityFormColumns<>(
        City.class
      , new LinkedHashMap<>() {{
          put(City_.NAME, new TextField(City_.NAME));
          put(City_.TYP, new ManyToOneField<>(City_.TYP
          , typesDbGrid
          , CityType::getName
          , CityType::getFullname
          , List.of(CityType_.NAME, CityType_.FULLNAME)
          , CityDbGridColumns.CITY_TYPE_FILL_REQUEST
          ));
          put(City_.STATE, new StateField(City_.STATE, statesDbGrid));
          put(City_.DISTRICT, new DistrictField(City_.DISTRICT, districtsDbGrid));
        }}
      , securityMgr
      )
    , validator
    );
  }

}
