package biz.softfor.vaadin.user;

import biz.softfor.user.jpa.Role;
import biz.softfor.user.jpa.User;
import biz.softfor.user.jpa.UserGroup;
import biz.softfor.user.jpa.UserGroupWor;
import biz.softfor.user.jpa.UserGroup_;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.vaadin.EntityForm;
import biz.softfor.vaadin.EntityFormColumns;
import biz.softfor.vaadin.field.ToManyField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import jakarta.validation.Validator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserGroupForm extends EntityForm<Integer, UserGroup, UserGroupWor> {

  public UserGroupForm(
    SecurityMgr securityMgr
  , Validator validator
  , UserGridFieldColumns userColumns
  , UsersDbGrid usersDbGrid
  , RoleGridFieldColumns roleColumns
  , RolesDbGrid rolesDbGrid
  ) {
    super(UserGroup.TITLE
    , new EntityFormColumns(
        securityMgr
      , UserGroup.class
      , UserGroupWor.class
      , new LinkedHashMap<String, Component>() {{
          put(UserGroup_.NAME, new TextField(UserGroup_.NAME));
          put(UserGroup_.USERS, new ToManyField<>(
            UserGroup_.USERS
          , UserGroup.class
          , HashSet<User>::new
          , userColumns
          , usersDbGrid
          , securityMgr
          ));
          put(UserGroup_.ROLES, new ToManyField<>(
            UserGroup_.ROLES
          , UserGroup.class
          , HashSet<Role>::new
          , roleColumns
          , rolesDbGrid
          , securityMgr
          ));
        }}
      )
    , validator
    );
  }

}
