package biz.softfor.vaadin.user;

import biz.softfor.user.jpa.Role;
import biz.softfor.user.jpa.RoleRequest;
import biz.softfor.user.jpa.RoleWor;
import biz.softfor.user.jpa.Role_;
import biz.softfor.user.jpa.UserGroup;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.vaadin.EntityView;
import biz.softfor.vaadin.MainLayout;
import biz.softfor.vaadin.field.grid.GridField;
import biz.softfor.vaadin.field.grid.GridFieldColumns;
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
@Route(value = RolesView.PATH, layout = MainLayout.class)
public class RolesView extends EntityView<Long, Role, RoleWor> {

  public final static String PATH = "role";

  public RolesView(
    RolesDbGrid dbGrid
  , GridFieldColumns<Integer, UserGroup> userGroupColumns
  , RoleForm form
  , SecurityMgr securityMgr
  ) {
    super(dbGrid
    , RoleRequest.Update.class
    , RoleRequest.Delete.class
    , new GridFields<>(securityMgr, Role.class, new GridField<>(
        Role_.GROUPS
      , UserGroup.class
      , HashSet<UserGroup>::new
      , userGroupColumns
      ))
    , form
    , securityMgr
    );
  }

}
