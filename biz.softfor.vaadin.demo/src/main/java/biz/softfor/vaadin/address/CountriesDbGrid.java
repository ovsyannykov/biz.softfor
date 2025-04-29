package biz.softfor.vaadin.address;

import biz.softfor.address.jpa.Country;
import biz.softfor.address.jpa.CountryWor;
import biz.softfor.address.spring.CountrySvc;
import biz.softfor.vaadin.dbgrid.DbGrid;
import biz.softfor.vaadin.dbgrid.DbGridColumns;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CountriesDbGrid extends DbGrid<Short, Country, CountryWor> {

  public CountriesDbGrid(CountrySvc service, CountryDbGridColumns columns) {
    super(service, columns, DbGridColumns.EMPTY);
  }

}
