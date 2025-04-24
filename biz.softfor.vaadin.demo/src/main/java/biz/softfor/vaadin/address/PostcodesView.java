package biz.softfor.vaadin.address;

import biz.softfor.address.jpa.Postcode;
import biz.softfor.address.jpa.PostcodeWor;
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
@Route(value = PostcodesView.PATH, layout = MainLayout.class)
public class PostcodesView extends EntityView<Integer, Postcode, PostcodeWor> {

  public final static String PATH = "postcode";

  public PostcodesView
  (PostcodesDbGrid dbGrid, PostcodeForm form, SecurityMgr securityMgr) {
    super(dbGrid, GridFields.EMPTY, form, securityMgr);
  }

}
