package biz.softfor.vaadin.partner;

import biz.softfor.partner.api.PartnerFltr;
import biz.softfor.partner.jpa.Partner;
import biz.softfor.partner.jpa.PartnerWor;
import biz.softfor.partner.spring.PartnerSvc;
import biz.softfor.vaadin.dbgrid.DbGrid;
import biz.softfor.vaadin.dbgrid.DbGridColumns;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PartnersDbGrid
extends DbGrid<Long, Partner, PartnerWor, PartnerFltr> {

  public PartnersDbGrid(PartnerSvc service, PartnerDbGridColumns columns) {
    super(service, columns, DbGridColumns.EMPTY);
  }

}
