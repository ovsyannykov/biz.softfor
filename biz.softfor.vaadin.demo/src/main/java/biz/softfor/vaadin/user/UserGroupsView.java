package biz.softfor.vaadin.user;

import biz.softfor.user.jpa.Role;
import biz.softfor.user.jpa.User;
import biz.softfor.user.jpa.UserGroup;
import biz.softfor.user.jpa.UserGroupWor;
import biz.softfor.user.jpa.UserGroup_;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.vaadin.EntityView;
import biz.softfor.vaadin.MainLayout;
import biz.softfor.vaadin.field.grid.GridField;
import biz.softfor.vaadin.field.grid.GridFields;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.util.HashSet;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@AnonymousAllowed
@Route(value = UserGroupsView.PATH, layout = MainLayout.class)
public class UserGroupsView extends EntityView<Integer, UserGroup, UserGroupWor> {

  public final static String PATH = "userGroup";

  public UserGroupsView(
    UserGroupsDbGrid dbGrid
  , UserGridFieldColumns userColumns
  , RoleGridFieldColumns roleColumns
  , UserGroupForm form
  , SecurityMgr securityMgr
  ) {
    super(
      dbGrid
    , new GridFields<>(
        securityMgr
      , UserGroup.class
      , new GridField<>(
          UserGroup_.USERS
        , User.class
        , HashSet<User>::new
        , userColumns
        )
      , new GridField<>(
          UserGroup_.ROLES
        , Role.class
        , HashSet<Role>::new
        , roleColumns
        )
      )
    , form
    , securityMgr
    );
  }

}
