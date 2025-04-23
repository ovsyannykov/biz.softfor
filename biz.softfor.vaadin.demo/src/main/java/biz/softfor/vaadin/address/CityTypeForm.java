package biz.softfor.vaadin.address;

import biz.softfor.address.jpa.CityType;
import biz.softfor.address.jpa.CityTypeWor;
import biz.softfor.address.jpa.CityType_;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.vaadin.EntityForm;
import biz.softfor.vaadin.EntityFormColumns;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import jakarta.validation.Validator;
import java.util.LinkedHashMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CityTypeForm extends EntityForm<Short, CityType, CityTypeWor> {

  public CityTypeForm(SecurityMgr securityMgr, Validator validator) {
    super(CityType.TITLE
    , new EntityFormColumns(
        CityType.class
      , new LinkedHashMap<String, Component>() {{
          TextField name = new TextField(CityType_.NAME);
          put(CityType_.NAME, name);
          TextField fullname = new TextField(CityType_.FULLNAME);
          fullname.addBlurListener(e -> {
            if(StringUtils.isBlank(fullname.getValue())) {
              fullname.setValue(name.getValue());
            }
          });
          put(CityType_.FULLNAME, fullname);
        }}
      , securityMgr
      )
    , validator
    );
  }

}
