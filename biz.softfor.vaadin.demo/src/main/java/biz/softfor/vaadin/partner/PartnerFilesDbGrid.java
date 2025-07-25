package biz.softfor.vaadin.partner;

import biz.softfor.partner.api.PartnerFileFltr;
import biz.softfor.partner.jpa.PartnerFile;
import biz.softfor.partner.jpa.PartnerFileWor;
import biz.softfor.partner.spring.PartnerFileSvc;
import biz.softfor.vaadin.dbgrid.DbGrid;
import biz.softfor.vaadin.dbgrid.DbGridColumns;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PartnerFilesDbGrid
extends DbGrid<Long, PartnerFile, PartnerFileWor, PartnerFileFltr> {

  public PartnerFilesDbGrid
  (PartnerFileSvc service, PartnerFileDbGridColumns columns) {
    super(service, columns, DbGridColumns.EMPTY);
  }

}
