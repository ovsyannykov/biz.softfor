package biz.softfor.vaadin.partner;

import biz.softfor.partner.api.ContactTypeFltr;
import biz.softfor.partner.jpa.ContactType;
import biz.softfor.partner.jpa.ContactType_;
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
public class ContactTypeDbGridColumns extends DbGridColumns<Short, ContactType> {

  public static List<Order> DEFAULT_SORT
  = List.of(new Order(Order.Direction.ASC, ContactType_.NAME));

  public final static BiConsumer<ReadRequest<Short, ContactTypeFltr>, String>
  FILL_REQUEST = (request, lookingFor) -> {
    String like = "%" + lookingFor.toLowerCase() + "%";
    request.filter.and(new Expr(Expr.OR
    , new Expr(Expr.LIKE
      , new Expr(Expr.LOWER, ContactType_.NAME)
      , new Value(like)
      )
    , new Expr(Expr.LIKE
      , new Expr(Expr.LOWER, ContactType_.DESCR)
      , new Value(like)
      )
    ));
  };

  public ContactTypeDbGridColumns(SecurityMgr securityMgr) {
    super(
      ContactType.TABLE
    , securityMgr
    , ContactType.class
    , new TextDbGridColumn<>(ContactType_.NAME, ContactTypeFltr::setName)
    , new TextDbGridColumn<>(ContactType_.DESCR, ContactTypeFltr::setDescr)
    );
  }

  @Override
  public List<Order> sort() {
    return DEFAULT_SORT;
  }

}
