package biz.softfor.vaadin.user;

import biz.softfor.user.jpa.User;
import biz.softfor.user.jpa.UserGroup;
import biz.softfor.user.jpa.UserWor;
import biz.softfor.user.jpa.User_;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.vaadin.EntityFormColumns;
import biz.softfor.vaadin.field.ToManyField;
import biz.softfor.vaadin.security.ProfileForm;
import biz.softfor.vaadin.security.ProfileFormColumns;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.spring.annotation.SpringComponent;
import jakarta.validation.Validator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserForm extends ProfileForm {

  private static LinkedHashMap<String, Component> columns(
    UserGroupGridFieldColumns userGroupColumns
  , UserGroupsDbGrid userGroupsDbGrid
  , SecurityMgr securityMgr
  ) {
    LinkedHashMap<String, Component> result = ProfileFormColumns.columns();
    result.put(User_.GROUPS, new ToManyField<>(
      User_.GROUPS
    , User.class
    , HashSet<UserGroup>::new
    , userGroupColumns
    , userGroupsDbGrid
    , securityMgr
    ));
    return result;
  };

  public UserForm(
    SecurityMgr securityMgr
  , Validator validator
  , PasswordEncoder passwordEncoder
  , UserGroupGridFieldColumns userGroupColumns
  , UserGroupsDbGrid userGroupsDbGrid
  ) {
    super(User.TITLE
    , new EntityFormColumns(
        securityMgr
      , User.class
      , UserWor.class
      , columns(userGroupColumns, userGroupsDbGrid, securityMgr)
    )
    , validator
    , passwordEncoder
    );
    fields.remove(User_.PASSWORD);
  }

}
