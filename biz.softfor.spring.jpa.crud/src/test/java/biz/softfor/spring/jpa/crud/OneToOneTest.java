package biz.softfor.spring.jpa.crud;

import biz.softfor.jpa.crud.querygraph.ColumnDescr;
import biz.softfor.partner.jpa.Partner;
import biz.softfor.partner.jpa.PartnerDetails;
import biz.softfor.partner.jpa.PartnerDetailsWor;
import biz.softfor.partner.jpa.PartnerDetails_;
import biz.softfor.partner.jpa.PartnerRequest;
import biz.softfor.partner.jpa.PartnerWor;
import biz.softfor.partner.jpa.Partner_;
import biz.softfor.partner.jpa.PersonDetails_;
import biz.softfor.spring.jpa.crud.assets.PartnersTestBasic;
import biz.softfor.spring.jpa.crud.assets.TestPartners;
import biz.softfor.spring.sqllog.SqlCountValidator;
import static biz.softfor.util.StringUtil.field;
import biz.softfor.util.api.CommonResponse;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import static org.assertj.core.util.Lists.list;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Log
public class OneToOneTest extends PartnersTestBasic {

  @Test
  void create() throws Exception {
    int CREATE_IDX = DATA_SIZE;
    PartnerRequest.Create request = new PartnerRequest.Create();
    request.data = TestPartners.newPartnerWor.apply(LABEL, CREATE_IDX);
    request.data.setPartnerDetails
    (TestPartners.newPartnerDetailsWor.apply(LABEL, CREATE_IDX));
    SqlCountValidator validator = new SqlCountValidator(em);
    CommonResponse<PartnerWor> response = partnerSvc.create(request);
    jpaCheck.create("response", response);
    validator.insert(2).assertTotal();
    Long newId = response.getData(0).getId();
    Partner newData = TestPartners.newPartner.apply(LABEL, CREATE_IDX);
    newData.setPartnerDetails
    (TestPartners.newPartnerDetails.apply(LABEL, CREATE_IDX));
    newData.setId(newId);
    newData.getPartnerDetails().setId(response.getData(0).getPartnerDetails().getId());
    data.partnerDetails.data.add(newData.getPartnerDetails());
    data.partners.data.add(newData);
    data.partners.check(data.partners.allIdxs(), data.partnerIgnoringFields
    , TestPartners.PARTNER_FETCH_RELATIONS);
  }

  @Test
  void createWithNullValue() throws Exception {
    int CREATE_IDX = DATA_SIZE;
    PartnerRequest.Create request = new PartnerRequest.Create();
    request.data = TestPartners.newPartnerWor.apply(LABEL, CREATE_IDX);
    SqlCountValidator validator = new SqlCountValidator(em);
    CommonResponse<PartnerWor> response = partnerSvc.create(request);
    jpaCheck.create("response", response);
    validator.insert(1).assertTotal();
    Long newId = response.getData(0).getId();
    Partner newData = TestPartners.newPartner.apply(LABEL, CREATE_IDX);
    newData.setId(newId);
    data.partners.data.add(newData);
    data.partners.check(data.partners.allIdxs(), data.partnerIgnoringFields
    , TestPartners.PARTNER_FETCH_RELATIONS);
  }

  @Test
  void read() throws Exception {
    Partner sample = data.partners.data.get(1);
    List<String> fields = list(
      field(Partner_.PARTNER_DETAILS, PartnerDetails_.NOTE)
    , field(Partner_.PERSON_DETAILS, PersonDetails_.PASSPORT_NUMBER)
    , Partner_.PARTNER_NAME
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

  @AllArgsConstructor
  static class UpdateParams {

    final String description;
    final Integer idx;
    final List<String> fields;
    final int total;
    final SqlCountValidator.Builder validatorBldr;
    final Consumer<UpdateCtx> updater;

  }

  @AllArgsConstructor
  static class UpdateCtx {

    final UpdateParams params;
    final PartnerRequest.Update request;
    final TestPartners data;
    final Partner partner;
    final String label;
    final EntityManager em;

  }

  static Stream<UpdateParams> update() {
    List<UpdateParams> p = new ArrayList<>();
    p.add(new UpdateParams(
      "Try to update the nonexistent partner.partnerDetails"
    , 1, null, 1
    , SqlCountValidator.builder().select(1).insert(1).update(1)
    , ctx -> {
        ctx.em.createNativeQuery("delete from " + PartnerDetails.TABLE
        + " where " + PartnerDetailsWor.ID + "=" + ctx.partner.getId())
        .executeUpdate();
        PartnerDetailsWor partnerDetails = new PartnerDetailsWor();
        partnerDetails.setNote("NNNN");
        ctx.request.data = new PartnerWor();
        ctx.request.data.setPartnerDetails(partnerDetails);
        ctx.data.partners.data.get(ctx.params.idx).getPartnerDetails().setNote(partnerDetails.getNote());
      }
    ));
    p.add(new UpdateParams(
      "Update partner.partnerDetails.note='NNNN'"
    , DATA_SIZE - 1, null, 1
    , SqlCountValidator.builder().update(1).select(1)
    , ctx -> {
        PartnerDetailsWor partnerDetails = new PartnerDetailsWor();
        partnerDetails.setNote("NNNN");
        ctx.request.data = new PartnerWor();
        ctx.request.data.setPartnerDetails(partnerDetails);
        ctx.data.partners.data.get(ctx.params.idx).getPartnerDetails()
        .setNote(partnerDetails.getNote());
       }
    ));
    p.add(new UpdateParams(
      "Update partner.partnerDetails to empty"
    , DATA_SIZE - 1, null, 0
    , SqlCountValidator.builder()
    , ctx -> {
        ctx.request.data = new PartnerWor();
        ctx.request.data.setPartnerDetails(new PartnerDetailsWor());
      }
    ));
    p.add(new UpdateParams(
      "Update partner.partnerDetails=null"
    , DATA_SIZE - 1, list(Partner_.PARTNER_DETAILS), 1
    , SqlCountValidator.builder().delete(1)
    , ctx -> ctx.data.partners.data.get(ctx.params.idx).setPartnerDetails(null)
    ));
    p.add(new UpdateParams(
      "Update partner.personDetails.married=null"
    , DATA_SIZE - 1, list(field(Partner_.PERSON_DETAILS, PersonDetails_.MARRIED)), 1
    , SqlCountValidator.builder().update(1)
    , ctx -> ctx.data.partners.data.get(ctx.params.idx).getPersonDetails().setMarried(null)
    ));
    return p.stream()
    //.skip(4).limit(1)
    ;
  }

  @ParameterizedTest
  @MethodSource
  void update(UpdateParams params) {
    Partner partner = data.partners.data.get(params.idx);
    log.info(() -> "=".repeat(32) + "\n" + params.description
    + "\nid=" + partner.getId());
    PartnerRequest.Update request = new PartnerRequest.Update();
    request.filter.assignId(partner.getId());
    request.fields = params.fields;
    params.updater
    .accept(new UpdateCtx(params, request, data, partner, LABEL, em));
    SqlCountValidator validator = params.validatorBldr.entityManager(em).build();
    CommonResponse<Partner> response = partnerSvc.update(request);
    jpaCheck.update("response", response, params.total);
    validator.assertTotal();
    data.partners.check(list(params.idx), data.userIgnoringFields
    , TestPartners.PARTNER_FETCH_RELATIONS);
  }

}
