package biz.softfor.vaadin.partner;

import biz.softfor.partner.jpa.LocationType;
import biz.softfor.partner.jpa.LocationTypeRequest;
import biz.softfor.partner.jpa.LocationTypeWor;
import biz.softfor.partner.spring.LocationTypeSvc;
import biz.softfor.vaadin.dbgrid.DbGrid;
import biz.softfor.vaadin.dbgrid.DbGridColumns;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LocationTypesDbGrid
extends DbGrid<Short, LocationType, LocationTypeWor> {

  public LocationTypesDbGrid
  (LocationTypeSvc service, LocationTypeDbGridColumns columns) {
    super(service, LocationTypeRequest.Read.class, columns, DbGridColumns.EMPTY);
  }

}
