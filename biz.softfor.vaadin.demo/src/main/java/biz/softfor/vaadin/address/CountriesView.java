package biz.softfor.vaadin.address;

import biz.softfor.address.jpa.Country;
import biz.softfor.address.jpa.CountryRequest;
import biz.softfor.address.jpa.CountryWor;
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
@Route(value = CountriesView.PATH, layout = MainLayout.class)
public class CountriesView extends EntityView<Short, Country, CountryWor> {

  public final static String PATH = "country";

  public CountriesView(
    CountriesDbGrid dbGrid
  , CountryForm form
  , SecurityMgr securityMgr
  ) {
    super(dbGrid
    , CountryRequest.Update.class
    , CountryRequest.Delete.class
    , GridFields.EMPTY
    , form
    , securityMgr
    );
  }

}
