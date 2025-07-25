package biz.softfor.vaadin.partner;

import biz.softfor.partner.api.ContactFltr;
import biz.softfor.partner.jpa.Contact;
import biz.softfor.partner.jpa.ContactWor;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.vaadin.EntityView;
import biz.softfor.vaadin.MainLayout;
import biz.softfor.vaadin.field.grid.GridFields;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@AnonymousAllowed
@Route(value = ContactsView.PATH, layout = MainLayout.class)
public class ContactsView
extends EntityView<Long, Contact, ContactWor, ContactFltr> {

  public final static String PATH = "contact";

  public ContactsView
  (ContactsDbGrid dbGrid, ContactForm form, SecurityMgr securityMgr) {
    super(dbGrid, GridFields.EMPTY, form, securityMgr);
  }

}
