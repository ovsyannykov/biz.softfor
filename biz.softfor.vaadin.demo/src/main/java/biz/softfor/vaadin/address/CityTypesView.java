package biz.softfor.vaadin.address;

import biz.softfor.address.jpa.CityType;
import biz.softfor.address.jpa.CityTypeRequest;
import biz.softfor.address.jpa.CityTypeWor;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.vaadin.EntityView;
import biz.softfor.vaadin.MainLayout;
import biz.softfor.vaadin.field.grid.GridFields;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@AnonymousAllowed
@Route(value = CityTypesView.PATH, layout = MainLayout.class)
public class CityTypesView extends EntityView<Short, CityType, CityTypeWor> {

  public final static String PATH = "citytype";

  public CityTypesView
  (CityTypesDbGrid dbGrid, CityTypeForm form, SecurityMgr securityMgr) {
    super(dbGrid
    , CityTypeRequest.Update.class
    , CityTypeRequest.Delete.class
    , GridFields.EMPTY
    , form
    , securityMgr
    );
  }

}
