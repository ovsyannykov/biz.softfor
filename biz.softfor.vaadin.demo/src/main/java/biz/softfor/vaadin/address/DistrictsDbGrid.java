package biz.softfor.vaadin.address;

import biz.softfor.address.jpa.District;
import biz.softfor.address.jpa.DistrictRequest;
import biz.softfor.address.jpa.DistrictWor;
import biz.softfor.address.spring.DistrictSvc;
import biz.softfor.vaadin.dbgrid.DbGrid;
import biz.softfor.vaadin.dbgrid.DbGridColumns;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DistrictsDbGrid extends DbGrid<Integer, District, DistrictWor> {

  public DistrictsDbGrid(DistrictSvc service, DistrictDbGridColumns columns) {
    super(service, DistrictRequest.Read.class, columns, DbGridColumns.EMPTY);
  }

}
