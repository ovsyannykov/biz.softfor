package biz.softfor.spring.jpa.crud;

import biz.softfor.jpa.crud.querygraph.ColumnDescr;
import biz.softfor.partner.jpa.LocationType;
import biz.softfor.partner.jpa.LocationTypeWor;
import biz.softfor.partner.jpa.Partner;
import biz.softfor.partner.jpa.PartnerRequest;
import biz.softfor.partner.jpa.PartnerWor;
import biz.softfor.partner.jpa.Partner_;
import biz.softfor.spring.jpa.crud.assets.PartnersTestBasic;
import biz.softfor.spring.jpa.crud.assets.TestPartners;
import biz.softfor.spring.sqllog.SqlCountValidator;
import static biz.softfor.util.StringUtil.field;
import biz.softfor.util.api.CommonResponse;
import jakarta.persistence.PersistenceException;
import java.util.List;
import lombok.extern.java.Log;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.util.Lists.list;
import org.junit.jupiter.api.Test;

@Log
public class ManyToOneTest extends PartnersTestBasic {

  @Test
  void create() throws Exception {
    int CREATE_I = 0;
    LocationType locationType = data.locationTypes.data.get(0);
    Partner parent = data.partners.data.get(0);
    PartnerRequest.Create request = new PartnerRequest.Create();
    request.data = TestPartners.newPartnerWor.apply(LABEL, CREATE_I);
    request.data.setLocationTypeId(locationType.getId());
    request.data.setParentId(parent.getId());
    SqlCountValidator validator = new SqlCountValidator(em);
    CommonResponse<PartnerWor> response = partnerSvc.create(request);
    jpaCheck.create("response", response);
    validator.insert(1).assertTotal();
    Partner newData = TestPartners.newPartner.apply(LABEL, CREATE_I);
    newData.setId(response.getData(0).getId());
    newData.setLocationType(locationType);
    newData.setParent(parent);
    data.partners.data.add(newData);
    data.partners.check(data.users.allIdxs(), data.partnerIgnoringFields
    , TestPartners.PARTNER_FETCH_RELATIONS);
  }

  @Test
  void createWithBadJoinColumn() throws ReflectiveOperationException {
    Class<?> EXPECTED_EXCEPTION = PersistenceException.class;
    int CREATE_I = 0;
    Short BAD_JOIN_ID = -31416;
    PartnerRequest.Create request = new PartnerRequest.Create();
    request.data = TestPartners.newPartnerWor.apply(LABEL, CREATE_I);
    request.data.setLocationTypeId(BAD_JOIN_ID);
    assertThatThrownBy(() -> partnerSvc.create(request))
    .as(() -> "Invalid locationTypeId=" + BAD_JOIN_ID + " should throw the "
    + EXPECTED_EXCEPTION.getName())
    .isInstanceOf(EXPECTED_EXCEPTION);
  }

  @Test
  void read() throws Exception {
    Partner sample = data.partners.data.get(1);
    List<String> fields = list(
      PartnerWor.PARTNER_NAME
    , field(Partner_.LOCATION_TYPE, LocationTypeWor.NAME)
    , field(Partner_.PARENT, PartnerWor.PARTNER_NAME)
    );
    Partner expected = ColumnDescr.copyByFields(sample, Partner.class, fields);
    PartnerRequest.Read request = new PartnerRequest.Read();
    request.filter.assignId(sample.getId());
    request.fields = fields;
    SqlCountValidator validator = new SqlCountValidator(em);
    CommonResponse<Partner> response = partnerSvc.read(request);
    jpaCheck.resultData("response", response, expected);
    validator.select(1).assertTotal();
  }

  @Test
  void update() {
    int partnerIdx = 2;
    Partner partner = data.partners.data.get(partnerIdx);
    LocationType locationType = null;
    Partner parent = data.partners.data.get(0);
    log.info(() -> "=".repeat(32) +"\nid=" + partner.getId()
    + "\nlocationTypeId=" + locationType
    + "\nparentId=" + parent.getId());
    partner.setLocationType(locationType);
    partner.setParent(parent);
    PartnerRequest.Update request = new PartnerRequest.Update();
    request.filter.assignId(partner.getId());
    request.fields = list(PartnerWor.LOCATION_TYPE_ID);
    request.data = new PartnerWor();
    request.data.setParentId(parent.getId());
    SqlCountValidator validator = new SqlCountValidator(em);
    CommonResponse<Partner> response = partnerSvc.update(request);
    jpaCheck.update("response", response, 1);
    validator.update(1).assertTotal();
    data.partners.check(list(partnerIdx), data.partnerIgnoringFields
    , TestPartners.PARTNER_FETCH_RELATIONS);
  }

}
