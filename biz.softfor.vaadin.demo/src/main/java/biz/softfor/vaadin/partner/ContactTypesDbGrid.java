package biz.softfor.vaadin.partner;

import biz.softfor.partner.jpa.ContactType;
import biz.softfor.partner.jpa.ContactTypeWor;
import biz.softfor.partner.spring.ContactTypeSvc;
import biz.softfor.vaadin.dbgrid.DbGrid;
import biz.softfor.vaadin.dbgrid.DbGridColumns;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ContactTypesDbGrid
extends DbGrid<Short, ContactType, ContactTypeWor> {

  public final static String TITLE = "Contact types";

  public ContactTypesDbGrid
  (ContactTypeSvc service, ContactTypeDbGridColumns columns) {
    super(service, columns, DbGridColumns.EMPTY);
  }

}
