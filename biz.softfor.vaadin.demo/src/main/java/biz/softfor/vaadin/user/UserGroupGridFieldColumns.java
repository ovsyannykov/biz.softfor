package biz.softfor.vaadin.user;

import biz.softfor.user.jpa.UserGroup;
import biz.softfor.user.jpa.UserGroup_;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.util.api.Order;
import biz.softfor.vaadin.field.grid.GridFieldColumns;
import biz.softfor.vaadin.field.grid.TextGridFieldsColumn;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.util.List;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserGroupGridFieldColumns
extends GridFieldColumns<Integer, UserGroup> {

  public UserGroupGridFieldColumns(SecurityMgr securityMgr) {
    super(securityMgr
    , UserGroup.class
    , new TextGridFieldsColumn<>(UserGroup_.NAME, UserGroup::getName)
    );
  }

  @Override
  public List<Order> sort() {
    return UserGroupDbGridColumns.DEFAULT_SORT;
  }

}
