package biz.softfor.vaadin.address;

import biz.softfor.address.jpa.City;
import biz.softfor.address.jpa.City_;
import biz.softfor.address.jpa.Postcode;
import biz.softfor.address.jpa.PostcodeWor;
import biz.softfor.address.jpa.Postcode_;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.vaadin.EntityForm;
import biz.softfor.vaadin.EntityFormColumns;
import biz.softfor.vaadin.field.ManyToOneField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import jakarta.validation.Validator;
import java.util.LinkedHashMap;
import java.util.List;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PostcodeForm extends EntityForm<Integer, Postcode, PostcodeWor> {

  public PostcodeForm(
    SecurityMgr securityMgr
  , Validator validator
  , DistrictsDbGrid districts
  , CitiesDbGrid cities
  ) {
    super(Postcode.TITLE
    , new EntityFormColumns(
        securityMgr
      , Postcode.class
      , PostcodeWor.class
      , new LinkedHashMap<String, Component>() {{
          put(Postcode_.POSTCODE, new TextField(Postcode_.POSTCODE));
          DistrictField districtField
          = new DistrictField(Postcode_.DISTRICT, districts);
          districtField.setClearButtonVisible(true);
          put(Postcode_.DISTRICT, districtField);
          ManyToOneField<Integer, City> cityField = new ManyToOneField<>
          (Postcode_.CITY, cities, City::getName, City::getName, List.of(City_.NAME));
          cityField.setClearButtonVisible(true);
          put(Postcode_.CITY, cityField);
        }}
      )
    , validator
    );
  }

}
