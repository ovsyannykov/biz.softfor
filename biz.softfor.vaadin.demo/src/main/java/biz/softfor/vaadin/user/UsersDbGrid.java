package biz.softfor.vaadin.user;

import biz.softfor.user.api.UserFltr;
import biz.softfor.user.jpa.User;
import biz.softfor.user.jpa.UserWor;
import biz.softfor.user.spring.UserSvc;
import biz.softfor.vaadin.dbgrid.DbGrid;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UsersDbGrid extends DbGrid<Long, User, UserWor, UserFltr> {

  public UsersDbGrid
  (UserSvc service, UserDbGridColumns columns, UserDbGridFilters filters) {
    super(service, columns, filters);
  }

}
