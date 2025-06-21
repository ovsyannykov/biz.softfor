package biz.softfor.vaadin.address;

import biz.softfor.address.jpa.Country;
import biz.softfor.address.jpa.CountryWor;
import biz.softfor.address.jpa.Country_;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.vaadin.EntityForm;
import biz.softfor.vaadin.EntityFormColumns;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import jakarta.validation.Validator;
import java.util.LinkedHashMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CountryForm extends EntityForm<Short, Country, CountryWor> {

  public CountryForm(SecurityMgr securityMgr, Validator validator) {
    super(Country.TITLE
    , new EntityFormColumns<>(
        Country.class
      , new LinkedHashMap<>() {{
          TextField name = new TextField(Country_.NAME);
          put(Country_.NAME, name);
          TextField fullname = new TextField(Country_.FULLNAME);
          fullname.addBlurListener(e -> {
            if(StringUtils.isBlank(fullname.getValue())) {
              fullname.setValue(name.getValue());
            }
          });
          put(Country_.FULLNAME, fullname);
        }}
      , securityMgr
      )
    , validator
    );
  }

}
