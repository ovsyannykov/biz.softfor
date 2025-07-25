package biz.softfor.vaadin.partner;

import biz.softfor.partner.api.AppointmentFltr;
import biz.softfor.partner.jpa.Appointment;
import biz.softfor.partner.jpa.Appointment_;
import biz.softfor.partner.jpa.Contact;
import biz.softfor.partner.jpa.ContactDetails_;
import biz.softfor.partner.jpa.ContactType;
import biz.softfor.partner.jpa.ContactType_;
import biz.softfor.partner.jpa.ContactWor;
import biz.softfor.partner.jpa.Contact_;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.util.StringUtil;
import biz.softfor.vaadin.EntityForm;
import biz.softfor.vaadin.EntityFormColumns;
import biz.softfor.vaadin.field.ManyToOneField;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import jakarta.validation.Validator;
import java.util.LinkedHashMap;
import java.util.List;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ContactForm extends EntityForm<Long, Contact, ContactWor> {

  public ContactForm(
    SecurityMgr securityMgr
  , Validator validator
  , ContactTypesDbGrid contactTypes
  , PartnersBasicDbGrid partners
  , AppointmentsDbGrid appointments
  ) {
    super(Contact.TITLE
    , new EntityFormColumns<>(
        Contact.class
      , new LinkedHashMap<>() {{
          put(Contact_.DESCR, new TextField(Contact_.DESCR));
          put(Contact_.PARTNER, new PartnerField(Contact_.PARTNER, partners));
          put(Contact_.APPOINTMENT, new ManyToOneField<>(
            Contact_.APPOINTMENT
          , appointments
          , Appointment::getName
          , Appointment::getDescr
          , List.of(Appointment_.NAME, Appointment_.DESCR)
          , AppointmentDbGridColumns.FILL_REQUEST
          ));
          put(Contact_.CONTACT_TYPE, new ManyToOneField<>(
            Contact_.CONTACT_TYPE
          , contactTypes
          , ContactType::getName
          , ContactType::getDescr
          , List.of(ContactType_.NAME, ContactType_.DESCR)
          , ContactTypeDbGridColumns.FILL_REQUEST
          ));
          put(Contact_.IS_PUBLIC, new Checkbox(Contact_.IS_PUBLIC));
          put(
            StringUtil.field(Contact_.CONTACT_DETAILS, ContactDetails_.NOTE)
          , new TextField(ContactDetails_.NOTE)
          );
        }}
      , securityMgr
      )
    , validator
    );
  }

}
