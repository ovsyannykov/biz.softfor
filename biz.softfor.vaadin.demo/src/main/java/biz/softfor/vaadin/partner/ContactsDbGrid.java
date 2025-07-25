package biz.softfor.vaadin.partner;

import biz.softfor.partner.api.ContactFltr;
import biz.softfor.partner.jpa.Contact;
import biz.softfor.partner.jpa.ContactWor;
import biz.softfor.partner.spring.ContactSvc;
import biz.softfor.vaadin.dbgrid.DbGrid;
import biz.softfor.vaadin.dbgrid.DbGridColumns;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ContactsDbGrid
extends DbGrid<Long, Contact, ContactWor, ContactFltr> {

  public ContactsDbGrid(ContactSvc service, ContactDbGridColumns columns) {
    super(service, columns, DbGridColumns.EMPTY);
  }

}
