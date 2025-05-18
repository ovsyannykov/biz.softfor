package biz.softfor.vaadin.address;

import biz.softfor.address.jpa.City;
import biz.softfor.address.jpa.CityWor;
import biz.softfor.address.jpa.City_;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.vaadin.EntityForm;
import biz.softfor.vaadin.EntityFormColumns;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import jakarta.validation.Validator;
import java.util.LinkedHashMap;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CityForm extends EntityForm<Integer, City, CityWor> {

  public CityForm(
    SecurityMgr securityMgr
  , Validator validator
  , StatesDbGrid statesDbGrid
  , DistrictsDbGrid districtsDbGrid
  ) {
    super(City.TITLE
    , new EntityFormColumns(
        City.class
      , new LinkedHashMap<String, Component>() {{
          put(City_.NAME, new TextField(City_.NAME));
          StateField sf = new StateField(City_.STATE, statesDbGrid);
          put(City_.STATE, sf);
          DistrictField df = new DistrictField(City_.DISTRICT, districtsDbGrid);
          put(City_.DISTRICT, df);
        }}
      , securityMgr
      )
    , validator
    );
  }

}
