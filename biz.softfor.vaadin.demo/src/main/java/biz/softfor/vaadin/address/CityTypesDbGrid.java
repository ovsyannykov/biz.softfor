package biz.softfor.vaadin.address;

import biz.softfor.address.api.CityTypeFltr;
import biz.softfor.address.jpa.CityType;
import biz.softfor.address.jpa.CityTypeWor;
import biz.softfor.address.spring.CityTypeSvc;
import biz.softfor.vaadin.dbgrid.DbGrid;
import biz.softfor.vaadin.dbgrid.DbGridColumns;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CityTypesDbGrid
extends DbGrid<Short, CityType, CityTypeWor, CityTypeFltr> {

  public CityTypesDbGrid(CityTypeSvc service, CityTypeDbGridColumns columns) {
    super(service, columns, DbGridColumns.EMPTY);
  }

}
