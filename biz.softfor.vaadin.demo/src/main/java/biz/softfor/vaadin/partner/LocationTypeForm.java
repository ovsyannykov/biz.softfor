package biz.softfor.vaadin.partner;

import biz.softfor.partner.jpa.LocationType;
import biz.softfor.partner.jpa.LocationTypeWor;
import biz.softfor.partner.jpa.LocationType_;
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
public class LocationTypeForm
extends EntityForm<Short, LocationType, LocationTypeWor> {

  public LocationTypeForm(SecurityMgr securityMgr, Validator validator) {
    super(LocationType.TITLE
    , new EntityFormColumns(
        securityMgr
      , LocationType.class
      , LocationTypeWor.class
      , new LinkedHashMap<String, Component>() {{
          TextField name = new TextField(LocationType_.NAME);
          put(LocationType_.NAME, name);
          TextField descr = new TextField(LocationType_.DESCR);
          descr.addBlurListener(e -> {
            if(StringUtils.isBlank(descr.getValue())) {
              descr.setValue(name.getValue());
            }
          });
          put(LocationType_.DESCR, descr);
        }}
      )
    , validator
    );
  }

}
