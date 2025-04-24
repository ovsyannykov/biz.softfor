package biz.softfor.vaadin.address;

import biz.softfor.address.jpa.District;
import biz.softfor.address.jpa.DistrictWor;
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
@Route(value = DistrictsView.PATH, layout = MainLayout.class)
public class DistrictsView extends EntityView<Integer, District, DistrictWor> {

  public final static String PATH = "district";

  public DistrictsView
  (DistrictsDbGrid dbGrid, DistrictForm form, SecurityMgr securityMgr) {
    super(dbGrid, GridFields.EMPTY, form, securityMgr);
  }

}
