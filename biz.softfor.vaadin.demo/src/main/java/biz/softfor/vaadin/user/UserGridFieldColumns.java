package biz.softfor.vaadin.user;

import biz.softfor.user.jpa.User;
import biz.softfor.user.jpa.User_;
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
public class UserGridFieldColumns extends GridFieldColumns<Long, User> {

  public UserGridFieldColumns(SecurityMgr securityMgr) {
    super(securityMgr
    , User.class
    , new TextGridFieldsColumn<>(User_.USERNAME, User::getUsername)
    , new TextGridFieldsColumn<>(User_.EMAIL, User::getEmail)
    );
  }

  @Override
  public List<Order> sort() {
    return UserDbGridColumns.DEFAULT_SORT;
  }

}
