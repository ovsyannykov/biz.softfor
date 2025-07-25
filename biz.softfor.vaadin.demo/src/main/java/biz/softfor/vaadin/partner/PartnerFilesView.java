package biz.softfor.vaadin.partner;

import biz.softfor.partner.api.PartnerFileFltr;
import biz.softfor.partner.jpa.PartnerFile;
import biz.softfor.partner.jpa.PartnerFileWor;
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
@Route(value = PartnerFilesView.PATH, layout = MainLayout.class)
public class PartnerFilesView
extends EntityView<Long, PartnerFile, PartnerFileWor, PartnerFileFltr> {

  public final static String PATH = "partnerfile";

  public PartnerFilesView
  (PartnerFilesDbGrid dbGrid, PartnerFileForm form, SecurityMgr securityMgr) {
    super(dbGrid, GridFields.EMPTY, form, securityMgr);
  }

}
