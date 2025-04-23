package biz.softfor.vaadin.user;

import biz.softfor.user.jpa.User;
import biz.softfor.user.jpa.UserGroup;
import biz.softfor.user.jpa.UserWor;
import biz.softfor.user.jpa.User_;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.vaadin.EntityForm;
import biz.softfor.vaadin.EntityFormColumns;
import biz.softfor.vaadin.field.ToManyField;
import biz.softfor.vaadin.security.ProfileFormColumns;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.spring.annotation.SpringComponent;
import jakarta.validation.Validator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserForm extends EntityForm<Long, User, UserWor> {

  private static LinkedHashMap<String, Component> columns(
    UserGroupGridFieldColumns userGroupColumns
  , UserGroupsDbGrid userGroupsDbGrid
  , SecurityMgr securityMgr
  ) {
    LinkedHashMap<String, Component> result = ProfileFormColumns.basic();
    ((HasValue)result.get(User_.EMAIL)).setReadOnly(true);
    ((HasValue)result.get(User_.USERNAME)).setReadOnly(true);
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
  , UserGroupGridFieldColumns userGroupColumns
  , UserGroupsDbGrid userGroupsDbGrid
  ) {
    super(
      User.TITLE
    , new EntityFormColumns<>(
        User.class
      , columns(userGroupColumns, userGroupsDbGrid, securityMgr)
      , securityMgr
      )
    , validator
    );
  }

}
