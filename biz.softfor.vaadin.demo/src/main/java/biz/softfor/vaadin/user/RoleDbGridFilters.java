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
public class RoleDbGridFilters extends DbGridColumns<Long, Role> {

  public RoleDbGridFilters(SecurityMgr securityMgr, UsersDbGridBasic users) {
    super("", securityMgr, Role.class
    , new DbGridColumn
      <Role, User, ManyToOneField<Long, User, UserFltr>, User, RoleFltr>(
        StringUtil.field(Role_.GROUPS, UserGroup_.USERS)
      , new ManyToOneField<>(
          User.TITLE
        , users
        , User::getUsername
        , User::getUsername
        , List.of(User_.USERNAME)
        , UserDbGridColumns.FILL_REQUEST
        )
      , null
      , (filter, component) -> {
          User v = component.getValue();
          if(v != null) {
            UserGroupFltr g = filter.getGroups();
            if(g == null) {
              filter.setGroups(g = new UserGroupFltr());
            }
            UserFltr u = g.getUsers();
            if(u == null) {
              g.setUsers(u = new UserFltr());
            }
            u.assignId(v.getId());
          }
        }
      )
    );
  }

}
