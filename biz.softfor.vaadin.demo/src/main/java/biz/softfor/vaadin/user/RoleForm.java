package biz.softfor.vaadin.user;

import biz.softfor.user.jpa.Role;
import biz.softfor.user.jpa.RoleWor;
import biz.softfor.user.jpa.Role_;
import biz.softfor.user.jpa.UserGroup;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.util.security.DefaultAccess;
import biz.softfor.vaadin.EntityForm;
import biz.softfor.vaadin.EntityFormColumns;
import biz.softfor.vaadin.field.ToManyField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.LongField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import jakarta.validation.Validator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RoleForm extends EntityForm<Long, Role, RoleWor> {

  public RoleForm(
    SecurityMgr securityMgr
  , Validator validator
  , UserGroupGridFieldColumns userGroupColumns
  , UserGroupsDbGrid userGroupsDbGrid
  ) {
    super(Role.TITLE
    , new EntityFormColumns(
        Role.class
      , new LinkedHashMap<String, Component>() {{
          LongField id = new LongField(Role_.ID);
          id.setReadOnly(true);
          put(Role_.ID, id);

          put(Role_.NAME, new TextField(Role_.NAME));

          ComboBox<DefaultAccess> defaultAccess
          = new ComboBox<>(Role_.DEFAULT_ACCESS, DefaultAccess.VALUES);
          put(Role_.DEFAULT_ACCESS, defaultAccess);

          Checkbox isUrl = new Checkbox(Role_.IS_URL);
          isUrl.setReadOnly(true);
          put(Role_.IS_URL, isUrl);

          Checkbox updateFor = new Checkbox(Role_.UPDATE_FOR);
          updateFor.setReadOnly(true);
          put(Role_.UPDATE_FOR, updateFor);

          put(Role_.DISABLED, new Checkbox(Role_.DISABLED));

          Checkbox orphan = new Checkbox(Role_.ORPHAN);
          orphan.setReadOnly(true);
          put(Role_.ORPHAN, orphan);

          Checkbox deniedForAll = new Checkbox(Role_.DENIED_FOR_ALL);
          deniedForAll.setReadOnly(true);
          put(Role_.DENIED_FOR_ALL, deniedForAll);

          TextField objName = new TextField(Role_.OBJ_NAME);
          objName.setReadOnly(true);
          put(Role_.OBJ_NAME, objName);

          put(Role_.DESCRIPTION, new TextField(Role_.DESCRIPTION));

          put(Role_.GROUPS, new ToManyField<>(
            Role_.GROUPS
          , Role.class
          , HashSet<UserGroup>::new
          , userGroupColumns
          , userGroupsDbGrid
          , securityMgr
          ));
        }}
      , securityMgr
      )
    , validator
    );
  }

}
