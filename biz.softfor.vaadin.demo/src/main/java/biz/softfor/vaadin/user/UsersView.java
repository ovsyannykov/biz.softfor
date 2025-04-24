package biz.softfor.vaadin.user;

import biz.softfor.user.jpa.User;
import biz.softfor.user.jpa.UserGroup;
import biz.softfor.user.jpa.UserWor;
import biz.softfor.user.jpa.User_;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.vaadin.EntityView;
import biz.softfor.vaadin.MainLayout;
import biz.softfor.vaadin.field.grid.GridField;
import biz.softfor.vaadin.field.grid.GridFields;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.util.HashSet;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@AnonymousAllowed
@Route(value = UsersView.PATH, layout = MainLayout.class)
@PreserveOnRefresh
public class UsersView extends EntityView<Long, User, UserWor> {

  public final static String PATH = "user";

  public UsersView(
    UsersDbGrid dbGrid
  , UserGroupGridFieldColumns userGroupColumns
  , UserForm form
  , SecurityMgr securityMgr
  ) {
    super(
      dbGrid
    , new GridFields<>(
        securityMgr
      , User.class
      , new GridField<>(
          User_.GROUPS
        , UserGroup.class
        , HashSet<UserGroup>::new
        , userGroupColumns
        )
      )
    , form
    , securityMgr
    );
  }

}
