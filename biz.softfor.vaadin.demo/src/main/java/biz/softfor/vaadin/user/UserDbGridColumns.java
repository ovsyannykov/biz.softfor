package biz.softfor.vaadin.user;

import biz.softfor.user.api.UserFltr;
import biz.softfor.user.jpa.User;
import biz.softfor.user.jpa.User_;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.util.api.Order;
import biz.softfor.util.api.ReadRequest;
import biz.softfor.util.api.filter.Expr;
import biz.softfor.util.api.filter.Value;
import biz.softfor.vaadin.dbgrid.DbGridColumns;
import biz.softfor.vaadin.dbgrid.TextDbGridColumn;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.util.List;
import java.util.function.BiConsumer;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserDbGridColumns extends DbGridColumns<Long, User> {

  public static List<Order> DEFAULT_SORT
  = List.of(new Order(Order.Direction.ASC, User_.USERNAME));

  public final static BiConsumer<ReadRequest<Long, UserFltr>, String>
  FILL_REQUEST = (request, lookingFor) -> {
    String like = "%" + lookingFor.toLowerCase() + "%";
    request.filter.and(new Expr(Expr.OR
    , new Expr(Expr.LIKE
      , new Expr(Expr.LOWER, User_.USERNAME)
      , new Value(like)
      )
    , new Expr(Expr.LIKE
      , new Expr(Expr.LOWER, User_.EMAIL)
      , new Value(like)
      )
    ));
  };

  public UserDbGridColumns(SecurityMgr securityMgr) {
    super(
      User.TABLE
    , securityMgr
    , User.class
    , new TextDbGridColumn<>(User_.USERNAME, UserFltr::setUsername)
    , new TextDbGridColumn<>(User_.EMAIL, UserFltr::setEmail)
    );
  }

  @Override
  public List<Order> sort() {
    return DEFAULT_SORT;
  }

}
