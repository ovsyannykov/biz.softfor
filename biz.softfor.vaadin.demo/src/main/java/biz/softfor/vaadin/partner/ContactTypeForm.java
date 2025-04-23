package biz.softfor.vaadin.partner;

import biz.softfor.partner.jpa.ContactType;
import biz.softfor.partner.jpa.ContactTypeWor;
import biz.softfor.partner.jpa.ContactType_;
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
public class ContactTypeForm
extends EntityForm<Short, ContactType, ContactTypeWor> {

  public ContactTypeForm(SecurityMgr securityMgr, Validator validator) {
    super(ContactType.TITLE
    , new EntityFormColumns(
        ContactType.class
      , new LinkedHashMap<String, Component>() {{
          TextField name = new TextField(ContactType_.NAME);
          put(ContactType_.NAME, name);
          TextField descr = new TextField(ContactType_.DESCR);
          descr.addBlurListener(e -> {
            if(StringUtils.isBlank(descr.getValue())) {
              descr.setValue(name.getValue());
            }
          });
          put(ContactType_.DESCR, descr);
        }}
      , securityMgr
      )
    , validator
    );
  }

}
