package biz.softfor.vaadin.address;

import biz.softfor.address.jpa.City;
import biz.softfor.address.jpa.CityRequest;
import biz.softfor.address.jpa.CityWor;
import biz.softfor.address.spring.CitySvc;
import biz.softfor.vaadin.dbgrid.DbGrid;
import biz.softfor.vaadin.dbgrid.DbGridColumns;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CitiesDbGrid extends DbGrid<Integer, City, CityWor> {

  public CitiesDbGrid(CitySvc service, CityDbGridColumns columns) {
    super(service, CityRequest.Read.class, columns, DbGridColumns.EMPTY);
  }

}
