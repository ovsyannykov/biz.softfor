package biz.softfor.vaadin.partner;

import biz.softfor.partner.jpa.Contact;
import biz.softfor.partner.jpa.ContactType;
import biz.softfor.partner.jpa.ContactType_;
import biz.softfor.partner.jpa.Contact_;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.util.BooleansEnum;
import biz.softfor.util.api.Order;
import biz.softfor.vaadin.VaadinUtil;
import biz.softfor.vaadin.dbgrid.ManyToOneGridColumnComponent;
import biz.softfor.vaadin.field.grid.BasicComboBoxGridFieldColumn;
import biz.softfor.vaadin.field.grid.BoolGridFieldColumn;
import biz.softfor.vaadin.field.grid.GridFieldColumn;
import biz.softfor.vaadin.field.grid.GridFieldColumns;
import biz.softfor.vaadin.field.grid.TextGridFieldsColumn;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.util.List;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ContactGridFieldColumns extends GridFieldColumns<Long, Contact> {

  public ContactGridFieldColumns
  (SecurityMgr securityMgr, ContactTypesDbGrid dbGrid) {
    super(
      securityMgr
    , Contact.class
    , new GridFieldColumn<>(
        Contact_.CONTACT_TYPE
      , new ManyToOneGridColumnComponent<>(
          Contact_.CONTACT_TYPE
        , dbGrid
        , ContactType::getName
        , ContactType::getDescr
        , List.of(ContactType_.NAME, ContactType_.DESCR)
        ).configure()
      , VaadinUtil.<Contact>defaultRenderer(m -> {
        ContactType e = m.getContactType();
        return e == null ? "" : e.getName();
      })
      , BasicComboBoxGridFieldColumn.defaultFilter(Contact::getContactType)
      )
    , new TextGridFieldsColumn<>(Contact_.DESCR, Contact::getDescr)
    , new BoolGridFieldColumn<>
      (Contact_.IS_PUBLIC, BooleansEnum.DEFINED_VALUES, Contact::getIsPublic)
    );
  }

  @Override
  public List<Order> sort() {
    return ContactDbGridColumns.DEFAULT_SORT;
  }

}
