package biz.softfor.vaadin.address;

import biz.softfor.address.api.CountryFltr;
import biz.softfor.address.jpa.Country;
import biz.softfor.address.jpa.Country_;
import biz.softfor.address.jpa.State;
import biz.softfor.address.jpa.StateWor;
import biz.softfor.address.jpa.State_;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.vaadin.EntityForm;
import biz.softfor.vaadin.EntityFormColumns;
import biz.softfor.vaadin.field.ManyToOneField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import jakarta.validation.Validator;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class StateForm extends EntityForm<Integer, State, StateWor> {

  public StateForm
  (SecurityMgr securityMgr, Validator validator, CountriesDbGrid countries) {
    super(State.TITLE
    , new EntityFormColumns<>(
        State.class
      , new LinkedHashMap<>() {{
          TextField name = new TextField(State_.NAME);
          put(State_.NAME, name);
          TextField fullname = new TextField(State_.FULLNAME);
          fullname.addBlurListener(e -> {
            if(StringUtils.isBlank(fullname.getValue())) {
              fullname.setValue(name.getValue());
            }
          });
          put(State_.FULLNAME, fullname);
          put(State_.COUNTRY, new ManyToOneField<>(State_.COUNTRY
          , countries
          , Country::getName
          , Country::getFullname
          , List.of(Country_.NAME, Country_.FULLNAME)
          , CountryDbGridColumns.FILL_REQUEST
          ));
        }}
      , securityMgr
      )
    , validator
    );
  }

}
