package biz.softfor.jpa.crud.querygraph;

import biz.softfor.spring.jpa.crud.TestConfigJpaCrud;
import biz.softfor.i18nspring.ConfigI18nSpring;
import biz.softfor.jpa.crud.querygraph.ColumnDescr;
import biz.softfor.jpa.crud.querygraph.DiffContext;
import biz.softfor.partner.jpa.PartnerDetailsWor;
import biz.softfor.partner.jpa.PartnerWor;
import biz.softfor.partner.jpa.PersonDetailsWor;
import biz.softfor.spring.jpa.crud.ConfigJpaCrud;
import biz.softfor.spring.jpa.properties.ConfigJpaProperties;
import biz.softfor.spring.objectmapper.ConfigObjectMapper;
import biz.softfor.util.api.AbstractRequest;
import biz.softfor.util.partner.PartnerType;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.list;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@DataJpaTest(showSql = false)
@EnableAutoConfiguration
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {
  TestConfigJpaCrud.class
, ConfigI18nSpring.class
, ConfigJpaCrud.class
, ConfigJpaProperties.class
, ConfigObjectMapper.class
, LocalValidatorFactoryBean.class
})
public class DiffTest {

  private final static String ALL2NULL = "all2Null";
  private final static String CHANGED = "changed";
  private final static String DATA = "data";

  private PersonDetailsWor personDetailsSample(Long id) {
    PersonDetailsWor result = new PersonDetailsWor();
    result.setId(id);
    result.setMiddlename(PersonDetailsWor.MIDDLENAME);
    result.setPassportDate(LocalDate.now());
    result.setPassportIssued(PersonDetailsWor.PASSPORT_ISSUED);
    result.setPassportNumber(9999);
    result.setPassportSeries("PS");
    return result;
  }

  private PartnerDetailsWor partnerDetailsSample(Long id) {
    PartnerDetailsWor result = new PartnerDetailsWor();
    result.setId(id);
    result.setNote(PartnerDetailsWor.NOTE);
    return result;
  }

  private PartnerWor partnerSample() {
    PartnerWor result = new PartnerWor();
    result.setId(11L);
    result.setPartnerName(PartnerWor.PARTNER_NAME);
    result.setPartnerRegdate(LocalDate.of(2022, 2, 24));
    result.setPartnerRegcode("8820220224");
    result.setAddress(PartnerWor.ADDRESS);
    result.setPartnerFullname(PartnerWor.PARTNER_FULLNAME);
    result.setTyp(PartnerType.LEGAL_ENTITY);
    result.setParentId(1L);
    result.setPostcodeId(22);
    result.setContactIds(list(1L, 2L, 3L));
    result.setLocationTypeId((short)33);
    result.setUserIds(list(11L, 12L, 13L, 14L));
    result.setPartnerDetails(partnerDetailsSample(result.getId()));
    result.setPersonDetails(personDetailsSample(result.getId()));
    result.setPartnerFileIds(list(101L, 102L, 103L));
    return result;
  }

  @Test
  void noChanges() throws Exception {
    PartnerWor vOld = partnerSample();
    PartnerWor v = partnerSample();
    DiffContext chkCtx = ColumnDescr.diff("", PartnerWor.class, v, vOld);
    assertThat(chkCtx.changed).as(CHANGED).isFalse();
    assertThat(chkCtx.data).as(DATA).isNull();
    assertThat(chkCtx.updateToNull).as(AbstractRequest.FIELDS).isNull();
    assertThat(chkCtx.all2Null).as(ALL2NULL).isFalse();
  }

  @Test
  void column() throws Exception {
    PartnerWor vOld = partnerSample();
    PartnerWor v = partnerSample();
    v.setPartnerName("NNNNN");
    v.setPartnerRegdate(LocalDate.of(2022, 03, 24));
    v.setPartnerFileIds(list(21L, 22L, 23L));
    DiffContext chkCtx = ColumnDescr.diff("", PartnerWor.class, v, vOld);
    assertThat(chkCtx.changed).as(CHANGED).isTrue();
    assertThat(chkCtx.updateToNull).as(AbstractRequest.FIELDS).isNull();
    assertThat(chkCtx.all2Null).as(ALL2NULL).isFalse();
    assertThat(((PartnerWor)chkCtx.data).getPartnerName())
    .as(PartnerWor.PARTNER_NAME).isEqualTo(v.getPartnerName());
    assertThat(((PartnerWor)chkCtx.data).getPartnerRegdate())
    .as(PartnerWor.PARTNER_REGDATE).isEqualTo(v.getPartnerRegdate());
    assertThat(((PartnerWor)chkCtx.data).getPartnerFileIds())
    .as(PartnerWor.PARTNER_FILE_IDS).isEqualTo(v.getPartnerFileIds());
  }

  @Test
  void columnToNull() throws Exception {
    PartnerWor vOld = partnerSample();
    PartnerWor v = partnerSample();
    v.setPartnerName(null);
    v.setPartnerRegdate(null);
    v.setPartnerFileIds(null);
    DiffContext chkCtx = ColumnDescr.diff("", PartnerWor.class, v, vOld);
    assertThat(chkCtx.changed).as(CHANGED).isTrue();
    assertThat(chkCtx.data).as(DATA).isNull();
    assertThat(chkCtx.updateToNull).as(AbstractRequest.FIELDS).isEqualTo
    (list(PartnerWor.PARTNER_NAME, PartnerWor.PARTNER_REGDATE, PartnerWor.PARTNER_FILE_IDS));
    assertThat(chkCtx.all2Null).as(ALL2NULL).isFalse();
  }

  @Test
  void oneToManyToEmpty() throws Exception {
    PartnerWor vOld = partnerSample();
    PartnerWor v = partnerSample();
    v.setPartnerFileIds(list());
    DiffContext chkCtx = ColumnDescr.diff("", PartnerWor.class, v, vOld);
    assertThat(chkCtx.changed).as(CHANGED).isTrue();
    assertThat(chkCtx.data).as(DATA).isNull();
    assertThat(chkCtx.updateToNull).as(AbstractRequest.FIELDS)
    .isEqualTo(list(PartnerWor.PARTNER_FILE_IDS));
    assertThat(chkCtx.all2Null).as(ALL2NULL).isFalse();
  }

  @Test
  void oneToOneToEmpty() throws Exception {
    PartnerWor vOld = partnerSample();
    PartnerWor v = partnerSample();
    v.setPersonDetails(new PersonDetailsWor());
    DiffContext chkCtx = ColumnDescr.diff("", PartnerWor.class, v, vOld);
    assertThat(chkCtx.changed).as(CHANGED).isTrue();
    assertThat(chkCtx.data).as(DATA).isNull();
    assertThat(chkCtx.updateToNull).as(AbstractRequest.FIELDS)
    .isEqualTo(list(PartnerWor.PERSON_DETAILS));
    assertThat(chkCtx.all2Null).as(ALL2NULL).isFalse();
  }

  @Test
  void oneToOneToNull() throws Exception {
    PartnerWor vOld = partnerSample();
    PartnerWor v = partnerSample();
    v.setPersonDetails(null);
    DiffContext chkCtx = ColumnDescr.diff("", PartnerWor.class, v, vOld);
    assertThat(chkCtx.changed).as(CHANGED).isTrue();
    assertThat(chkCtx.data).as(DATA).isNull();
    assertThat(chkCtx.updateToNull).as(AbstractRequest.FIELDS)
    .isEqualTo(list(PartnerWor.PERSON_DETAILS));
    assertThat(chkCtx.all2Null).as(ALL2NULL).isFalse();
  }

}
