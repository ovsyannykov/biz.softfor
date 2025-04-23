package biz.softfor.vaadin.partner;

import biz.softfor.partner.jpa.PartnerFile;
import biz.softfor.partner.jpa.PartnerFileWor;
import biz.softfor.vaadin.dbgrid.DbGrid;
import biz.softfor.vaadin.dbgrid.DbGridColumns;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PartnerFilesDbGrid extends DbGrid<Long, PartnerFile, PartnerFileWor> {

  public PartnerFilesDbGrid(PartnerFileDbGridColumns columns) {
    super(PartnerFile.class, columns, DbGridColumns.EMPTY);
  }

}
