package biz.softfor.vaadin.address;

import biz.softfor.address.jpa.City;
import biz.softfor.address.jpa.CityRequest;
import biz.softfor.address.jpa.CityWor;
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
@Route(value = CitiesView.PATH, layout = MainLayout.class)
public class CitiesView extends EntityView<Integer, City, CityWor> {

  public final static String PATH = "city";

  public CitiesView
  (CitiesDbGrid dbGrid, CityForm form, SecurityMgr securityMgr) {
    super(dbGrid
    , CityRequest.Update.class
    , CityRequest.Delete.class
    , GridFields.EMPTY
    , form
    , securityMgr
    );
  }

}
