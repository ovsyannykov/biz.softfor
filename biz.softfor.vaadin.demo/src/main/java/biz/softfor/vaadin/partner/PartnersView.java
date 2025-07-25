package biz.softfor.vaadin.partner;

import biz.softfor.partner.api.PartnerFltr;
import biz.softfor.partner.jpa.Contact;
import biz.softfor.partner.jpa.Partner;
import biz.softfor.partner.jpa.PartnerFile;
import biz.softfor.partner.jpa.PartnerWor;
import biz.softfor.partner.jpa.Partner_;
import biz.softfor.user.jpa.User;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.vaadin.EntityView;
import biz.softfor.vaadin.MainLayout;
import biz.softfor.vaadin.field.grid.GridField;
import biz.softfor.vaadin.field.grid.GridFields;
import biz.softfor.vaadin.user.UserGridFieldColumns;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.util.ArrayList;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@RouteAlias(value = "", layout = MainLayout.class)
@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Route(value = PartnersView.PATH, layout = MainLayout.class)
@AnonymousAllowed
public class PartnersView
extends EntityView<Long, Partner, PartnerWor, PartnerFltr> {

  public final static String PATH = "partner";

  public PartnersView(
    PartnersDbGrid dbGrid
  , ContactGridFieldColumns contactColumns
  , PartnerFileGridFieldColumns partnerFileColumns
  , UserGridFieldColumns userColumns
  , PartnerForm form
  , SecurityMgr securityMgr
  ) {
    super(
      dbGrid
    , new GridFields<>(
        securityMgr
      , Partner.class
      , new GridField<>(
          Partner_.CONTACTS
        , Contact.class
        , ArrayList<Contact>::new
        , contactColumns
        )
      , new GridField<>(
          Partner_.PARTNER_FILES
        , PartnerFile.class
        , ArrayList<PartnerFile>::new
        , partnerFileColumns
        )
      , new GridField<>(
          Partner_.USERS
        , User.class
        , ArrayList<User>::new
        , userColumns
        )
      )
    , form
    , securityMgr
    );
  }

}
