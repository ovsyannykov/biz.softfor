package biz.softfor.vaadin.user;

import biz.softfor.user.api.UserGroupFltr;
import biz.softfor.user.jpa.UserGroup;
import biz.softfor.user.jpa.UserGroup_;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.util.api.Order;
import biz.softfor.vaadin.dbgrid.DbGridColumns;
import biz.softfor.vaadin.dbgrid.TextDbGridColumn;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.util.List;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserGroupDbGridColumns extends DbGridColumns<Integer, UserGroup> {

  public static List<Order> DEFAULT_SORT
  = List.of(new Order(Order.Direction.ASC, UserGroup_.NAME));

  public UserGroupDbGridColumns(SecurityMgr securityMgr) {
    super(
      UserGroup.TABLE
    , securityMgr
    , UserGroup.class
    , new TextDbGridColumn<>(UserGroup_.NAME, UserGroupFltr::setName)
    );
  }

  @Override
  public List<Order> sort() {
    return DEFAULT_SORT;
  }

}
