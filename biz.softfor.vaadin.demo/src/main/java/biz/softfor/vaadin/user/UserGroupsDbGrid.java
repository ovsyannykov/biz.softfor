package biz.softfor.vaadin.user;

import biz.softfor.user.jpa.UserGroup;
import biz.softfor.user.jpa.UserGroupRequest;
import biz.softfor.user.jpa.UserGroupWor;
import biz.softfor.user.spring.UserGroupSvc;
import biz.softfor.vaadin.dbgrid.DbGrid;
import biz.softfor.vaadin.dbgrid.DbGridColumns;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserGroupsDbGrid extends DbGrid<Integer, UserGroup, UserGroupWor> {

  public UserGroupsDbGrid(UserGroupSvc service, UserGroupDbGridColumns columns) {
    super(service, UserGroupRequest.Read.class, columns, DbGridColumns.EMPTY);
  }

}
