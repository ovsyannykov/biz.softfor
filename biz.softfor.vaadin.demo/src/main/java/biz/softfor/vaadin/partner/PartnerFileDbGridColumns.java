package biz.softfor.vaadin.partner;

import biz.softfor.partner.api.PartnerFileFltr;
import biz.softfor.partner.api.PartnerFltr;
import biz.softfor.partner.jpa.PartnerFile;
import biz.softfor.partner.jpa.PartnerFile_;
import biz.softfor.partner.jpa.Partner_;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.util.StringUtil;
import biz.softfor.util.api.Order;
import biz.softfor.vaadin.dbgrid.DbGridColumns;
import biz.softfor.vaadin.dbgrid.TextDbGridColumn;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.util.List;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PartnerFileDbGridColumns extends DbGridColumns<Long, PartnerFile> {

  public static List<Order> DEFAULT_SORT
  = List.of(
    new Order
    (Order.Direction.ASC, StringUtil.field(PartnerFile_.PARTNER, Partner_.ID))
  , new Order(Order.Direction.ASC, PartnerFile_.DESCR)
  );

  public PartnerFileDbGridColumns
  (SecurityMgr securityMgr, PartnersBasicDbGrid partners) {
    super(
      PartnerFile.TABLE
    , securityMgr
    , PartnerFile.class
    , new PartnerBasicDbGridColumn<>(
        PartnerFile_.PARTNER
      , PartnerFile::getPartner
      , PartnerFileFltr::getPartner
      , PartnerFileFltr::setPartner
      , PartnerFltr::new
      , partners
      )
    , new TextDbGridColumn<>(PartnerFile_.DESCR, PartnerFileFltr::setDescr)
    , new TextDbGridColumn<>(PartnerFile_.URI, PartnerFileFltr::setUri)
    );
  }

  @Override
  public List<Order> sort() {
    return DEFAULT_SORT;
  }

}
