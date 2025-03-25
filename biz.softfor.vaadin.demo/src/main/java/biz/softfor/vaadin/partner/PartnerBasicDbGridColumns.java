package biz.softfor.vaadin.partner;

import biz.softfor.partner.jpa.Partner;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.util.api.Order;
import biz.softfor.vaadin.dbgrid.DbGridColumns;
import biz.softfor.vaadin.address.PostcodesDbGrid;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.util.List;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PartnerBasicDbGridColumns extends DbGridColumns<Long, Partner> {

  public PartnerBasicDbGridColumns(
    SecurityMgr securityMgr
  , PostcodesDbGrid postcodes
  , LocationTypesDbGrid locationTypes
  ) {
    super(
      Partner.TABLE
    , securityMgr
    , Partner.class
    , PartnerDbGridColumns.columns(postcodes, locationTypes)
    );
  }

  @Override
  public List<Order> sort() {
    return PartnerDbGridColumns.DEFAULT_SORT;
  }

}
