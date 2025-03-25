package biz.softfor.spring.jpa.crud;

import biz.softfor.partner.jpa.Contact;
import biz.softfor.partner.jpa.ContactRequest;
import biz.softfor.partner.jpa.ContactType;
import biz.softfor.partner.jpa.ContactWor;
import biz.softfor.partner.jpa.Contact_;
import biz.softfor.partner.jpa.Partner;
import biz.softfor.spring.jpa.crud.assets.PartnersTestBasic;
import biz.softfor.spring.jpa.crud.assets.TestPartners;
import biz.softfor.testutil.IgnoringFields;
import biz.softfor.util.api.CommonResponse;
import biz.softfor.spring.sqllog.SqlCountValidator;
import lombok.extern.java.Log;
import static org.assertj.core.util.Lists.list;
import static org.assertj.core.util.Sets.set;
import org.junit.jupiter.api.Test;

@Log
public class BooleanColumnTest extends PartnersTestBasic {

  private final static IgnoringFields contactIgnoringFields = new IgnoringFields(
    Contact.class
  , set(
      Contact_.APPOINTMENT
    , Contact_.CONTACT_DETAILS
    , Contact_.CONTACT_TYPE
    , Contact_.PARTNER
    )
  );

  @Test
  void create() throws Exception {
    int CREATE_IDX = DATA_SIZE;
    int PARTNER_IDX = 0;
    int TYPE_IDX = 0;
    Partner PARTNER = data.partners.data.get(PARTNER_IDX);
    ContactType TYPE = data.contactTypes.data.get(TYPE_IDX);
    ContactRequest.Create request = new ContactRequest.Create();
    request.data = TestPartners.newContactWor.apply(LABEL, CREATE_IDX);
    request.data.setPartnerId(PARTNER.getId());
    request.data.setTypeId(TYPE.getId());
    SqlCountValidator validator = new SqlCountValidator(em);
    CommonResponse<ContactWor> response = contactSvc.create(request);
    jpaCheck.create("response", response);
    validator.insert(1).assertTotal();
    Long newId = response.getData(0).getId();
    Contact newData = TestPartners.newContact.apply(LABEL, CREATE_IDX);
    newData.setPartner(PARTNER);
    newData.setContactType(TYPE);
    newData.setId(newId);
    data.contacts.data.add(newData);
    data.contacts.check(list(CREATE_IDX), contactIgnoringFields);
  }

  @Test
  void update() {
    int UPDATE_IDX = 0;
    Contact CONTACT = data.contacts.data.get(UPDATE_IDX);
    Long UPDATE_ID = CONTACT.getId();
    Boolean IS_PUBLIC = !CONTACT.getIsPublic();
    ContactRequest.Update request = new ContactRequest.Update();
    request.filter.assignId(UPDATE_ID);
    request.data = new ContactWor();
    request.data.setIsPublic(IS_PUBLIC);
    SqlCountValidator validator = new SqlCountValidator(em);
    CommonResponse<Contact> response = contactSvc.update(request);
    jpaCheck.update("response", response, 1);
    validator.update(1).assertTotal();
    CONTACT.setIsPublic(IS_PUBLIC);
    data.contacts.check(list(UPDATE_IDX), contactIgnoringFields);
  }

}
