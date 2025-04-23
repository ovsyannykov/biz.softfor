package biz.softfor.vaadin.user;

import biz.softfor.user.jpa.Role;
import biz.softfor.user.jpa.RoleWor;
import biz.softfor.vaadin.dbgrid.DbGrid;
import biz.softfor.vaadin.dbgrid.DbGridColumns;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RolesDbGrid extends DbGrid<Long, Role, RoleWor> {

  public RolesDbGrid(RoleDbGridColumns columns) {
    super(Role.class, columns, DbGridColumns.EMPTY);
  }

}
