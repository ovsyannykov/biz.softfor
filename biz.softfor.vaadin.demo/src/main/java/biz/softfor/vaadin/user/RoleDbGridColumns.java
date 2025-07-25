package biz.softfor.vaadin.user;

import biz.softfor.user.api.RoleFltr;
import biz.softfor.user.jpa.Role;
import biz.softfor.user.jpa.Role_;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.util.BooleansEnum;
import biz.softfor.util.api.Order;
import biz.softfor.util.api.ReadRequest;
import biz.softfor.util.api.filter.Expr;
import biz.softfor.util.api.filter.Value;
import biz.softfor.util.security.DefaultAccess;
import biz.softfor.vaadin.dbgrid.BoolDbGridColumn;
import biz.softfor.vaadin.dbgrid.ComboBoxDbGridColumn;
import biz.softfor.vaadin.dbgrid.DbGridColumns;
import biz.softfor.vaadin.dbgrid.NumberDbGridColumn;
import biz.softfor.vaadin.dbgrid.TextDbGridColumn;
import com.vaadin.flow.component.textfield.LongField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.util.List;
import java.util.function.BiConsumer;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RoleDbGridColumns extends DbGridColumns<Long, Role> {

  public static List<Order> DEFAULT_SORT = List.of(
    new Order(Order.Direction.ASC, Role_.OBJ_NAME)
  , new Order(Order.Direction.ASC, Role_.UPDATE_FOR)
  );

  public final static BiConsumer<ReadRequest<Long, RoleFltr>, String>
  FILL_REQUEST = (request, lookingFor) -> {
    String like = "%" + lookingFor.toLowerCase() + "%";
    request.filter.and(new Expr(Expr.OR
    , new Expr(Expr.LIKE
      , new Expr(Expr.LOWER, Role_.NAME)
      , new Value(like)
      )
    , new Expr(Expr.LIKE
      , new Expr(Expr.LOWER, Role_.DESCRIPTION)
      , new Value(like)
      )
    , new Expr(Expr.LIKE
      , new Expr(Expr.LOWER, Role_.OBJ_NAME)
      , new Value(like)
      )
    ));
  };

  public RoleDbGridColumns(SecurityMgr securityMgr) {
    super(Role.TABLE, securityMgr, Role.class
    , new TextDbGridColumn<>(Role_.NAME, RoleFltr::setName)
    , new BoolDbGridColumn<>
      (Role_.ORPHAN, BooleansEnum.DEFINED_VALUES, Role::getOrphan)
    , new BoolDbGridColumn<>
      (Role_.DENIED_FOR_ALL, BooleansEnum.DEFINED_VALUES, Role::getDeniedForAll)
    , new ComboBoxDbGridColumn<>(
        Role_.DEFAULT_ACCESS
      , DefaultAccess.VALUES
      , Role::getDefaultAccess
      , RoleFltr::setDefaultAccess
      )
    , new BoolDbGridColumn<>
      (Role_.IS_URL, BooleansEnum.DEFINED_VALUES, Role::getIsUrl)
    , new BoolDbGridColumn<>
      (Role_.UPDATE_FOR, BooleansEnum.DEFINED_VALUES, Role::getUpdateFor)
    , new TextDbGridColumn<>(Role_.OBJ_NAME, RoleFltr::setObjName)
    , new NumberDbGridColumn<Role, Long, LongField, RoleFltr>
      (Role_.ID, LongField.class, RoleFltr::assignId)
    , new TextDbGridColumn<>(Role_.DESCRIPTION, RoleFltr::setDescription)
    );
  }

  @Override
  public List<Order> sort() {
    return DEFAULT_SORT;
  }

}
