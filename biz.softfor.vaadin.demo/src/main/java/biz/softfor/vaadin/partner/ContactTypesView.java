package biz.softfor.vaadin.partner;

import biz.softfor.partner.jpa.ContactType;
import biz.softfor.partner.jpa.ContactTypeWor;
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
@Route(value = ContactTypesView.PATH, layout = MainLayout.class)
public class ContactTypesView
extends EntityView<Short, ContactType, ContactTypeWor> {

  public final static String PATH = "contacttype";

  public ContactTypesView
  (ContactTypesDbGrid dbGrid, ContactTypeForm form, SecurityMgr securityMgr) {
    super(dbGrid, GridFields.EMPTY, form, securityMgr);
  }

}
