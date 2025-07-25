package biz.softfor.vaadin.user;

import biz.softfor.user.api.RoleFltr;
import biz.softfor.user.jpa.Role;
import biz.softfor.user.jpa.RoleWor;
import biz.softfor.user.spring.RoleSvc;
import biz.softfor.vaadin.dbgrid.DbGrid;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RolesDbGrid extends DbGrid<Long, Role, RoleWor, RoleFltr> {

  public RolesDbGrid
  (RoleSvc service, RoleDbGridColumns columns, RoleDbGridFilters filters) {
    super(service, columns, filters);
  }

}
