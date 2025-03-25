package biz.softfor.vaadin.partner;

import biz.softfor.partner.jpa.Appointment;
import biz.softfor.partner.jpa.AppointmentWor;
import biz.softfor.partner.jpa.Appointment_;
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
public class AppointmentForm
extends EntityForm<Short, Appointment, AppointmentWor> {

  public AppointmentForm(SecurityMgr securityMgr, Validator validator) {
    super(Appointment.TITLE
    , new EntityFormColumns(
        securityMgr
      , Appointment.class
      , AppointmentWor.class
      , new LinkedHashMap<String, Component>() {{
          TextField name = new TextField(Appointment_.NAME);
          put(Appointment_.NAME, name);
          TextField descr = new TextField(Appointment_.DESCR);
          descr.addBlurListener(e -> {
            if(StringUtils.isBlank(descr.getValue())) {
              descr.setValue(name.getValue());
            }
          });
          put(Appointment_.DESCR, descr);
        }}
      )
    , validator
    );
  }

}
