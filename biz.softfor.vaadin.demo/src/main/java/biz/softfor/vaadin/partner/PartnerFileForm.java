package biz.softfor.vaadin.partner;

import biz.softfor.partner.jpa.PartnerFile;
import biz.softfor.partner.jpa.PartnerFileWor;
import biz.softfor.partner.jpa.PartnerFile_;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.vaadin.EntityForm;
import biz.softfor.vaadin.EntityFormColumns;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import jakarta.validation.Validator;
import java.util.LinkedHashMap;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PartnerFileForm
extends EntityForm<Long, PartnerFile, PartnerFileWor> {

  public PartnerFileForm
  (SecurityMgr securityMgr, Validator validator, PartnersBasicDbGrid partners) {
    super(PartnerFile.TITLE
    , new EntityFormColumns<>(
        PartnerFile.class
      , new LinkedHashMap<>() {{
          put(PartnerFile_.PARTNER, new PartnerField(PartnerFile_.PARTNER, partners));
          put(PartnerFile_.DESCR, new TextField(PartnerFile_.DESCR));
          put(PartnerFile_.URI, new TextField(PartnerFile_.URI));
        }}
      , securityMgr
      )
    , validator
    );
  }

}
