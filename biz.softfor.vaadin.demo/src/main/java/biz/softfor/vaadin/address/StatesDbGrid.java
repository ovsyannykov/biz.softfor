package biz.softfor.vaadin.address;

import biz.softfor.address.jpa.State;
import biz.softfor.address.jpa.StateRequest;
import biz.softfor.address.jpa.StateWor;
import biz.softfor.address.spring.StateSvc;
import biz.softfor.vaadin.dbgrid.DbGrid;
import biz.softfor.vaadin.dbgrid.DbGridColumns;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class StatesDbGrid extends DbGrid<Integer, State, StateWor> {

  public StatesDbGrid(StateSvc service, StateDbGridColumns columns) {
    super(service, StateRequest.Read.class, columns, DbGridColumns.EMPTY);
  }

}
