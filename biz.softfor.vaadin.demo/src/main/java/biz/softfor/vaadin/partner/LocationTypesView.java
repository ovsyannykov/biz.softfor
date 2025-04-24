package biz.softfor.vaadin.partner;

import biz.softfor.partner.jpa.LocationType;
import biz.softfor.partner.jpa.LocationTypeWor;
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
@Route(value = LocationTypesView.PATH, layout = MainLayout.class)
public class LocationTypesView
extends EntityView<Short, LocationType, LocationTypeWor> {

  public final static String PATH = "locationtype";

  public LocationTypesView
  (LocationTypesDbGrid dbGrid, LocationTypeForm form, SecurityMgr securityMgr) {
    super(dbGrid, GridFields.EMPTY, form, securityMgr);
  }

}
