package biz.softfor.vaadin.partner;

import biz.softfor.partner.jpa.PartnerFile;
import biz.softfor.partner.jpa.PartnerFile_;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.util.api.Order;
import biz.softfor.vaadin.field.grid.GridFieldColumns;
import biz.softfor.vaadin.field.grid.TextGridFieldsColumn;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.util.List;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PartnerFileGridFieldColumns
extends GridFieldColumns<Long, PartnerFile> {

  public PartnerFileGridFieldColumns(SecurityMgr securityMgr) {
    super(securityMgr
    , PartnerFile.class
    , new TextGridFieldsColumn<>(PartnerFile_.DESCR, PartnerFile::getDescr)
    , new TextGridFieldsColumn<>(PartnerFile_.URI, PartnerFile::getUri)
    );
  }

  @Override
  public List<Order> sort() {
    return PartnerFileDbGridColumns.DEFAULT_SORT;
  }

}
