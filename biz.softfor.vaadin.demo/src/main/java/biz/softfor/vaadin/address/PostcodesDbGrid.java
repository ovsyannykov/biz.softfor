package biz.softfor.vaadin.address;

import biz.softfor.address.jpa.Postcode;
import biz.softfor.address.jpa.PostcodeRequest;
import biz.softfor.address.jpa.PostcodeWor;
import biz.softfor.address.spring.PostcodeSvc;
import biz.softfor.vaadin.dbgrid.DbGrid;
import biz.softfor.vaadin.dbgrid.DbGridColumns;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PostcodesDbGrid extends DbGrid<Integer, Postcode, PostcodeWor> {

  public PostcodesDbGrid(PostcodeSvc service, PostcodeDbGridColumns columns) {
    super(service, PostcodeRequest.Read.class, columns, DbGridColumns.EMPTY);
  }

}
