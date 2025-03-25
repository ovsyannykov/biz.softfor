package biz.softfor.vaadin.address;

import biz.softfor.address.jpa.District;
import biz.softfor.address.jpa.DistrictWor;
import biz.softfor.address.jpa.District_;
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
public class DistrictForm extends EntityForm<Integer, District, DistrictWor> {

  public DistrictForm
  (SecurityMgr securityMgr, Validator validator, StatesDbGrid states) {
    super(District.TITLE
    , new EntityFormColumns(
        securityMgr
      , District.class
      , DistrictWor.class
      , new LinkedHashMap<String, Component>() {{
          TextField name = new TextField(District_.NAME);
          put(District_.NAME, name);
          TextField fullname = new TextField(District_.FULLNAME);
          fullname.addBlurListener(e -> {
            if(StringUtils.isBlank(fullname.getValue())) {
              fullname.setValue(name.getValue());
            }
          });
          put(District_.FULLNAME, fullname);
          put(District_.STATE, new StateField(District_.STATE, states));
        }}
      )
    , validator
    );
  }

}
