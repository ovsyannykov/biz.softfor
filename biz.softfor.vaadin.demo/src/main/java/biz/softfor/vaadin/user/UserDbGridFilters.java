package biz.softfor.vaadin.user;

import biz.softfor.user.api.RoleFltr;
import biz.softfor.user.api.UserFltr;
import biz.softfor.user.api.UserGroupFltr;
import biz.softfor.user.jpa.Role;
import biz.softfor.user.jpa.Role_;
import biz.softfor.user.jpa.User;
import biz.softfor.user.jpa.UserGroup_;
import biz.softfor.user.jpa.User_;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.util.StringUtil;
import biz.softfor.vaadin.dbgrid.DbGridColumn;
import biz.softfor.vaadin.dbgrid.DbGridColumns;
import biz.softfor.vaadin.field.ManyToOneField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.util.List;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserDbGridFilters extends DbGridColumns<Long, User> {

  public UserDbGridFilters(SecurityMgr securityMgr, RolesDbGridBasic roles) {
    super("", securityMgr, User.class
    , new DbGridColumn<User, Role, ManyToOneField<Long, Role>, Role, UserFltr>(
        StringUtil.field(User_.GROUPS, UserGroup_.ROLES)
      , new ManyToOneField<>(
          Role.TITLE
        , roles
        , Role::getName
        , Role::getDescription
        , List.of(Role_.NAME)
        )
      , null
      , (filter, component) -> {
          Role v = component.getValue();
          if(v != null) {
            UserGroupFltr g = filter.getGroups();
            if(g == null) {
              filter.setGroups(g = new UserGroupFltr());
            }
            RoleFltr u = g.getRoles();
            if(u == null) {
              g.setRoles(u = new RoleFltr());
            }
            u.assignId(v.getId());
          }
        }
      )
    );
  }

}
