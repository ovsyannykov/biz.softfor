package biz.softfor.spring.rest.demo;

import biz.softfor.i18nspring.I18n;
import biz.softfor.i18nutil.I18nUtil;
import biz.softfor.jpa.crud.querygraph.ColumnDescr;
import biz.softfor.partner.api.PartnerDto;
import biz.softfor.partner.api.PartnerRequest;
import biz.softfor.partner.api.PartnerResponse;
import biz.softfor.partner.api.PersonDetailsDto;
import biz.softfor.partner.jpa.Partner;
import biz.softfor.partner.jpa.PartnerWor;
import biz.softfor.partner.jpa.PersonDetailsWor;
import biz.softfor.spring.i18nrest.ConfigSpringI18nRest;
import biz.softfor.testutil.spring.RestAssuredCall;
import biz.softfor.user.spring.rest.SecurityTest;
import static biz.softfor.user.spring.rest.SecurityTest.authorize;
import biz.softfor.util.Constants;
import biz.softfor.util.Json;
import biz.softfor.util.StringUtil;
import static biz.softfor.util.StringUtil.field;
import biz.softfor.util.api.AbstractRequest;
import biz.softfor.util.api.BasicResponse;
import biz.softfor.util.api.CommonResponse;
import biz.softfor.util.api.StdPath;
import biz.softfor.util.partner.PartnerType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Supplier;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.list;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
public class ValidationTest {

  @LocalServerPort
  private int port;

  @Autowired
  private ObjectMapper om;

  @Autowired
  private MessageSource ms;

  @Autowired
  private I18n i18n;

  @Autowired
  private Validator validator;

  private RestAssuredCall testSvc;
  private Locale testLocale = I18nUtil.UKRAINIAN;
  //private Locale testLocale = Locale.ENGLISH;

  @BeforeEach
  public void beforeEach() {
    testSvc = new RestAssuredCall(om);
    testSvc.headers.put(ConfigSpringI18nRest.ACCEPT_LANGUAGE, testLocale.toString());
    RestAssured.basePath = StdPath.ROOT;
    RestAssured.defaultParser = Parser.JSON;
    RestAssured.port = port;
  }

  @Test
  public void createWithEmptyData() throws Exception {
    PartnerRequest.Create request = new PartnerRequest.Create();
    PartnerResponse res = testSvc.call(PartnerResponse.class
    , PartnerRequest.CREATE_METHOD, PartnerRequest.CREATE_PATH, request);
    Supplier<String> resJson = () -> "\nres=" + Json.serializep(om, res);
    assertThat(res.getStatus()).as(() -> "res." + BasicResponse.STATUS + resJson.get())
    .isEqualTo(BasicResponse.BAD_REQUEST);
    assertThat(res.getTotal()).as("res." + CommonResponse.TOTAL + resJson.get())
    .isEqualTo(0L);
    assertThat(res.getDescr()).as("res." + BasicResponse.DESCR + resJson.get())
    .isEqualTo(ms.getMessage(StringUtil.fieldToName(AbstractRequest.DATA), null, testLocale)
    + ": " + ms.getMessage("jakarta.validation.constraints.NotNull.message", null, testLocale));
  }

  @Test
  public void createWithInvalidDateAndSize() throws Exception {
    PartnerRequest.Create request = new PartnerRequest.Create();
    request.data = new PartnerDto();
    request.data.setTyp(PartnerType.ORGANIZATION);
    request.data.setPartnerName("qwerty");
    request.data.setPartnerRegdate(LocalDate.of(3000, 10, 20));
    request.data.setPartnerRegcode("123456789012345678901234");
    request.data.setAddress("Big st., 1");
    request.data.setPartnerFullname("Fullname");
    request.data.setPartnerDetails(null);
    request.data.setPersonDetails(null);
    PartnerResponse res = testSvc.call(PartnerResponse.class
    , PartnerRequest.CREATE_METHOD, PartnerRequest.CREATE_PATH, request);
    Supplier<String> resJson = () -> "\nres=" + Json.serializep(om, res);
    assertThat(res.getStatus()).as(() -> "res." + BasicResponse.STATUS + resJson.get())
    .isEqualTo(BasicResponse.BAD_REQUEST);
    assertThat(res.getTotal()).as("res." + CommonResponse.TOTAL + resJson.get())
    .isEqualTo(0L);
    Size sizeAnno = PartnerDto.class.getDeclaredField(PartnerDto.PARTNER_REGCODE)
    .getAnnotation(Size.class);
    assertThat(res.getDescr()).as("res." + BasicResponse.DESCR + resJson.get())
    .contains(
      ms.getMessage(StringUtil.fieldToName(PartnerDto.PARTNER_REGDATE), null, testLocale)
      + ": " + ms.getMessage("jakarta.validation.constraints.PastOrPresent.message", null, testLocale)
    , ms.getMessage(StringUtil.fieldToName(PartnerDto.PARTNER_REGCODE), null, testLocale)
      + ": " + ms.getMessage("jakarta.validation.constraints.Size.message", null, testLocale)
      .replace("{min}", Integer.toString(sizeAnno.min()))
      .replace("{max}", Integer.toString(sizeAnno.max()))
    );
  }

  @Test
  public void createNonPersonWithPersonDetails() throws Exception {
    PartnerRequest.Create request = new PartnerRequest.Create();
    request.data = new PartnerDto();
    request.data.setTyp(PartnerType.ORGANIZATION);
    request.data.setPartnerName("qwerty");
    request.data.setPartnerRegdate(LocalDate.of(2024, 1, 3));
    request.data.setPartnerRegcode("12345678901");
    request.data.setAddress("Big st., 1");
    request.data.setPartnerFullname("Fullname");
    request.data.setPartnerDetails(null);
    PersonDetailsDto pd = new PersonDetailsDto();
    pd.setMarried(Boolean.FALSE);
    pd.setMiddlename("Bred");
    pd.setPassportDate(LocalDate.of(2024, 2, 24));
    pd.setPassportIssued("DEP");
    pd.setPassportNumber("1");
    pd.setPassportSeries("NN");
    request.data.setPersonDetails(pd);
    PartnerResponse res = testSvc.call(PartnerResponse.class
    , PartnerRequest.CREATE_METHOD, PartnerRequest.CREATE_PATH, request);
    Supplier<String> resJson = () -> "\nres=" + Json.serializep(om, res);
    assertThat(res.getStatus()).as(() -> "res." + BasicResponse.STATUS + resJson.get())
    .isEqualTo(BasicResponse.BAD_REQUEST);
    assertThat(res.getTotal()).as("res." + CommonResponse.TOTAL + resJson.get())
    .isEqualTo(0L);
    assertThat(res.getDescr()).as("res." + BasicResponse.DESCR + resJson.get())
    .isEqualTo("partnerTypeIsPersonOrPersonDetailsIsEmpty"
    + ": " + ms.getMessage(Partner.Person_details_must_be_empty_for_the_non_person_partner_type, null, testLocale));
  }

  @Test
  public void validateNonPersonWithEmptyPersonDetails() throws Exception {
    PartnerWor data = new PartnerWor();
    data.setTyp(PartnerType.ORGANIZATION);
    data.setPersonDetails(new PersonDetailsWor());
    data.getPersonDetails().setMiddlename("");
    data.getPersonDetails().setMarried(Boolean.FALSE);
    List<String> vf = list(PartnerWor.TYP);
    Set<ConstraintViolation<?>> validate = new HashSet<>();
    ColumnDescr.validate(validator, validate, PartnerWor.class, data, vf);
    assertThat(validate).as("Validate results").isEmpty();
  }

  @Test
  public void updateWithEmptyDataAndFields() throws Exception {
    PartnerRequest.Update request = new PartnerRequest.Update();
    PartnerResponse res = testSvc.call(PartnerResponse.class
    , PartnerRequest.UPDATE_METHOD, PartnerRequest.UPDATE_PATH, request);
    Supplier<String> resJson = () -> "\nres=" + Json.serializep(om, res);
    assertThat(res.getStatus()).as(() -> "res." + BasicResponse.STATUS + resJson.get())
    .isEqualTo(BasicResponse.BAD_REQUEST);
    assertThat(res.getTotal()).as("res." + CommonResponse.TOTAL + resJson.get())
    .isEqualTo(0L);
    assertThat(res.getDescr()).as("res." + BasicResponse.DESCR + resJson.get())
    .isEqualTo("valid: Request 'data' or 'fields' must be not empty.");
  }

  @Test
  public void updateWithEmptyData() throws Exception {
    PartnerRequest.Update request = new PartnerRequest.Update();
    request.token = authorize(SecurityTest.ADMIN_DTO, testSvc, om);
    request.fields = list(PartnerDto.PARTNER_REGCODE);
    request.filter.assignId(0L);
    PartnerResponse res = testSvc.call(PartnerResponse.class
    , PartnerRequest.UPDATE_METHOD, PartnerRequest.UPDATE_PATH, request);
    Supplier<String> resJson = () -> "\nres=" + Json.serializep(om, res);
    assertThat(res.getStatus()).as(() -> "res." + BasicResponse.STATUS + resJson.get())
    .isEqualTo(BasicResponse.BAD_REQUEST);
    assertThat(res.getTotal()).as("res." + CommonResponse.TOTAL + resJson.get())
    .isEqualTo(0L);
    assertThat(res.getDescr()).as("res." + BasicResponse.DESCR + resJson.get())
    .contains(
      ms.getMessage(StringUtil.fieldToName(PartnerDto.PARTNER_REGCODE), null, testLocale)
      + ": " + ms.getMessage("jakarta.validation.constraints.NotBlank.message", null, testLocale)
    );
  }

  @Test
  public void updateOneToOneField() throws Exception {
    PartnerRequest.Update request = new PartnerRequest.Update();
    request.token = authorize(SecurityTest.ADMIN_DTO, testSvc, om);
    request.data = new PartnerDto();
    request.data.setPersonDetails(new PersonDetailsDto());
    request.data.getPersonDetails().setPassportSeries("Q");
    request.filter.assignId(0L);
    PartnerResponse res = testSvc.call(PartnerResponse.class
    , PartnerRequest.UPDATE_METHOD, PartnerRequest.UPDATE_PATH, request);
    Supplier<String> resJson = () -> "\nres=" + Json.serializep(om, res);
    assertThat(res.getStatus())
    .as(() -> "res." + BasicResponse.STATUS + resJson.get())
    .isEqualTo(BasicResponse.BAD_REQUEST);
    assertThat(res.getTotal()).as("res." + CommonResponse.TOTAL + resJson.get())
    .isEqualTo(0L);
    Size sizeAnno
    = PersonDetailsDto.class.getDeclaredField(PersonDetailsDto.PASSPORT_SERIES)
    .getAnnotation(Size.class);
    assertThat(res.getDescr()).as("res." + BasicResponse.DESCR + resJson.get())
    .contains(
      ms.getMessage(StringUtil.fieldToName(PersonDetailsDto.PASSPORT_SERIES), null, testLocale)
      + ": " + ms.getMessage("jakarta.validation.constraints.Size.message", null, testLocale)
      .replace("{min}", Integer.toString(sizeAnno.min()))
      .replace("{max}", Integer.toString(sizeAnno.max()))
    );
  }

  @Test
  public void updateOneToOneFieldToNull() throws Exception {
    PartnerRequest.Update request = new PartnerRequest.Update();
    request.token = authorize(SecurityTest.ADMIN_DTO, testSvc, om);
    request.fields = list(field(PartnerDto.PERSON_DETAILS, PersonDetailsDto.PASSPORT_SERIES));
    request.filter.assignId(0L);
    PartnerResponse res = testSvc.call(PartnerResponse.class
    , PartnerRequest.UPDATE_METHOD, PartnerRequest.UPDATE_PATH, request);
    Supplier<String> resJson = () -> "\nres=" + Json.serializep(om, res);
    assertThat(res.getStatus())
    .as(() -> "res." + BasicResponse.STATUS + resJson.get())
    .isEqualTo(BasicResponse.BAD_REQUEST);
    assertThat(res.getTotal()).as("res." + CommonResponse.TOTAL + resJson.get())
    .isEqualTo(0L);
    assertThat(res.getDescr()).as("res." + BasicResponse.DESCR + resJson.get())
    .contains(
      ms.getMessage(StringUtil.fieldToName(PersonDetailsDto.PASSPORT_SERIES), null, testLocale)
      + ": " + ms.getMessage("jakarta.validation.constraints.NotBlank.message", null, testLocale)
    );
  }

  @Test
  public void messages() throws Exception {
    assertThat(ms.getMessage(Constants.Unsupported_operation
    , new Object[] { "update" }, Locale.US))
    .isEqualTo("Unsupported operation: update.");
    LocaleContextHolder.setLocale(I18nUtil.UKRAINIAN);
    assertThat(i18n.message(Constants.Unsupported_operation, "update"))
    .isEqualTo("Непідтримувана операція: update.");
    LocaleContextHolder.setLocale(Locale.getDefault());
  }

}
