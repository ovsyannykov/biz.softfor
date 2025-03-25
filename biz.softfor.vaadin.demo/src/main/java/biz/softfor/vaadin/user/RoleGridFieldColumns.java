package biz.softfor.vaadin.user;

import biz.softfor.user.jpa.Role;
import biz.softfor.user.jpa.Role_;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.util.BooleansEnum;
import biz.softfor.util.api.Order;
import biz.softfor.vaadin.field.grid.BoolGridFieldColumn;
import biz.softfor.vaadin.field.grid.GridFieldColumns;
import biz.softfor.vaadin.field.grid.NumberGridFieldColumn;
import biz.softfor.vaadin.field.grid.TextGridFieldsColumn;
import com.vaadin.flow.component.textfield.LongField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.util.List;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RoleGridFieldColumns extends GridFieldColumns<Long, Role> {

  public RoleGridFieldColumns(SecurityMgr securityMgr) {
    super(securityMgr
    , Role.class
    , new TextGridFieldsColumn<>(Role_.NAME, Role::getName)
    , new BoolGridFieldColumn<>(
        Role_.IS_URL
      , BooleansEnum.DEFINED_VALUES
      , Role::getIsUrl
      )
    , new BoolGridFieldColumn<>(
        Role_.UPDATE_FOR
      , BooleansEnum.DEFINED_VALUES
      , Role::getUpdateFor
      )
    , new BoolGridFieldColumn<>(
        Role_.DISABLED
      , BooleansEnum.DEFINED_VALUES
      , Role::getDisabled
      )
    , new BoolGridFieldColumn<>(
        Role_.ORPHAN
      , BooleansEnum.DEFINED_VALUES
      , Role::getDisabled
      )
    , new TextGridFieldsColumn<>(Role_.OBJ_NAME, Role::getObjName)
    , new NumberGridFieldColumn<Role, Long, LongField>
      (Role_.ID, LongField.class, Role::getId)
    );
  }

  @Override
  public List<Order> sort() {
    return RoleDbGridColumns.DEFAULT_SORT;
  }

}
