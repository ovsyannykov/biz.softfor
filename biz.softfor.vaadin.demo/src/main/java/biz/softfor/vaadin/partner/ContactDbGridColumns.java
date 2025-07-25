package biz.softfor.vaadin.partner;

import biz.softfor.partner.api.AppointmentFltr;
import biz.softfor.partner.api.ContactFltr;
import biz.softfor.partner.api.ContactTypeFltr;
import biz.softfor.partner.api.PartnerFltr;
import biz.softfor.partner.jpa.Appointment;
import biz.softfor.partner.jpa.Appointment_;
import biz.softfor.partner.jpa.Contact;
import biz.softfor.partner.jpa.ContactType;
import biz.softfor.partner.jpa.ContactType_;
import biz.softfor.partner.jpa.Contact_;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.util.BooleansEnum;
import biz.softfor.util.api.Order;
import biz.softfor.vaadin.VaadinUtil;
import biz.softfor.vaadin.dbgrid.BoolDbGridColumn;
import biz.softfor.vaadin.dbgrid.DbGridColumns;
import biz.softfor.vaadin.dbgrid.ManyToOneDbGridColumn;
import biz.softfor.vaadin.dbgrid.TextDbGridColumn;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.util.List;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ContactDbGridColumns extends DbGridColumns<Long, Contact> {

  public static List<Order> DEFAULT_SORT
  = List.of(new Order(Order.Direction.ASC, Contact_.DESCR));

  public ContactDbGridColumns(
    SecurityMgr securityMgr
  , ContactTypesDbGrid contactTypes
  , PartnersBasicDbGrid partners
  , AppointmentsDbGrid appointments
  ) {
    super(
      Contact.TABLE
    , securityMgr
    , Contact.class
    , new TextDbGridColumn<>(Contact_.DESCR, ContactFltr::setDescr)
    , new BoolDbGridColumn<>
      (Contact_.IS_PUBLIC, BooleansEnum.DEFINED_VALUES, Contact::getIsPublic)
    , new ManyToOneDbGridColumn<>(
        Contact_.CONTACT_TYPE
      , VaadinUtil.defaultRenderer(m -> {
          ContactType e = m.getContactType();
          return e == null ? "" : e.getName();
        })
      , ManyToOneDbGridColumn.defaultFilter(
          ContactFltr::getContactType
        , ContactFltr::setContactType
        , ContactTypeFltr::new
        )
      , contactTypes
      , ContactType::getName
      , ContactType::getName
      , List.of(ContactType_.NAME)
      , ContactTypeDbGridColumns.FILL_REQUEST
      )
    , new PartnerBasicDbGridColumn<>(
        Contact_.PARTNER
      , Contact::getPartner
      , ContactFltr::getPartner
      , ContactFltr::setPartner
      , PartnerFltr::new
      , partners
      )
    , new ManyToOneDbGridColumn<>(
        Contact_.APPOINTMENT
      , VaadinUtil.defaultRenderer(m -> {
          Appointment e = m.getAppointment();
          return e == null ? "" : e.getName();
        })
      , ManyToOneDbGridColumn.defaultFilter(
          ContactFltr::getAppointment
        , ContactFltr::setAppointment
        , AppointmentFltr::new
        )
      , appointments
      , Appointment::getName
      , Appointment::getDescr
      , List.of(Appointment_.NAME, Appointment_.DESCR)
      , AppointmentDbGridColumns.FILL_REQUEST
      )
    );
  }

  @Override
  public List<Order> sort() {
    return DEFAULT_SORT;
  }

}
