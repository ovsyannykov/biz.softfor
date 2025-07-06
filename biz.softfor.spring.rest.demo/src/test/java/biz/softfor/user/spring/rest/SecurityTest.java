package biz.softfor.user.spring.rest;

import biz.softfor.address.api.StateDto;
import biz.softfor.address.api.StateRequest;
import biz.softfor.address.api.StateResponse;
import biz.softfor.partner.api.ContactDetailsRequest;
import biz.softfor.partner.api.ContactDetailsResponse;
import biz.softfor.partner.api.ContactDetailsRto;
import biz.softfor.partner.api.LocationTypeDto;
import biz.softfor.partner.api.PartnerDto;
import biz.softfor.partner.api.PartnerRequest;
import biz.softfor.partner.api.PartnerResponse;
import biz.softfor.partner.api.PartnerRto;
import biz.softfor.partner.api.PersonDetailsDto;
import biz.softfor.partner.api.PersonDetailsRto;
import biz.softfor.partner.spring.rest.ConfigPartnerRest;
import biz.softfor.spring.messagesi18n.I18n;
import biz.softfor.testutil.spring.RestAssuredCall;
import biz.softfor.user.api.UserDto;
import biz.softfor.user.api.UserGroupDto;
import biz.softfor.user.api.UserGroupRequest;
import biz.softfor.user.api.UserGroupResponse;
import biz.softfor.user.api.UserRequest;
import biz.softfor.user.api.UserResponse;
import biz.softfor.user.api.UserRto;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.user.spring.rest.testassets.TeztEntity;
import biz.softfor.util.Json;
import biz.softfor.util.Range;
import static biz.softfor.util.StringUtil.field;
import biz.softfor.util.api.AbstractRequest;
import biz.softfor.util.api.AuthResponse;
import biz.softfor.util.api.BasicResponse;
import biz.softfor.util.api.StdPath;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import lombok.extern.java.Log;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.list;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(classes = { ConfigPartnerRest.class, ConfigUserRest.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
@EntityScan(basePackageClasses = { TeztEntity.class })
@ExtendWith(OutputCaptureExtension.class)
@Log
public class SecurityTest {

  //these values are specified in the src/test/resources/db/h2/1_1__testdata.sql
  public final static UserRto ADMIN_DTO = new UserRto();
  static {
    ADMIN_DTO.setUsername("admin");
    ADMIN_DTO.setPassword("12345678");
    ADMIN_DTO.setEmail("admin@t.co");
  };
  public final static Long ADMIN_ID = 1L;
  private final static Long PARTNER_ID = 1L;
  private final static String PARTNER_GROUP = "ADMIN";
  private final static String PARTNER_NAME = "TestPartnerName";
  private final static Integer GROUP_ID = 1;
  private final static String GROUP_NAME = "ADMIN";

  @Autowired
  private ObjectMapper om;

  @Autowired
  private I18n i18n;

  @LocalServerPort
  private int port;

  private RestAssuredCall testSvc;

  public static String authorize
  (UserRto user, RestAssuredCall testSvc, ObjectMapper om)
  throws Exception {
    UserRequest.Create req = new UserRequest.Create(user);
    AuthResponse res = testSvc.call(AuthResponse.class
    , StdPath.LOGIN_METHOD, StdPath.LOGIN, req);
    Supplier<String> msg = () -> "authorization=" + Json.serializep(om, res);
    assertThat(res.getStatus()).as(msg).isEqualTo(BasicResponse.OK);
    assertThat(res.getData()).as(msg).isNotNull().size().isEqualTo(2);
    return res.getData(AuthResponse.ACCESS_TOKEN_ID).token;
  }

  @BeforeEach
  public void beforeEach() {
    RestAssured.basePath = StdPath.ROOT;
    RestAssured.defaultParser = Parser.JSON;
    RestAssured.port = port;
    testSvc = new RestAssuredCall(om);
  }

  @Test
  public void happyPath() throws Exception {
    UserRto user = new UserRto();
    user.setUsername("happyPath");
    user.setPassword("l2345678");
    user.setEmail("happyPath@t.co");
    String INVALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6W10sInVzZXJJZCI6NTM0LCJqdGkiOiIxIiwic3ViIjoiaGFwcHlQYXRoIiwiaWF0IjoxNjkwMjE5NTc3LCJleHAiOjE2OTAyMjAxNzd9.6060chPaWe-b3y2pvPY7PcJhwblgCq6Q1QEDqEmf9ds";

    AbstractRequest logoutWithInvalidTokenReq = new AbstractRequest();
    logoutWithInvalidTokenReq.token = INVALID_TOKEN;
    BasicResponse logoutWithInvalidTokenRes = testSvc.call(BasicResponse.class
    , StdPath.LOGOUT_METHOD, StdPath.LOGOUT, logoutWithInvalidTokenReq);
    Supplier<String> logoutWithInvalidTokeMsg = () ->
    "logoutWithInvalidTokeRes=" + Json.serializep(om, logoutWithInvalidTokenRes);
    assertThat(logoutWithInvalidTokenRes.getStatus()).as(logoutWithInvalidTokeMsg)
    .isEqualTo(BasicResponse.ACCESS_DENIED);

    AbstractRequest nonAuthenticatedReq = new AbstractRequest();
    nonAuthenticatedReq.token = INVALID_TOKEN;
    BasicResponse nonAuthenticatedRes = testSvc.call(BasicResponse.class
    , StdPath.REFRESH_TOKEN_METHOD, StdPath.REFRESH_TOKEN, nonAuthenticatedReq);
    assertThat(nonAuthenticatedRes.getStatus()).as("nonAuthenticatedRes.status")
    .isEqualTo(BasicResponse.ACCESS_DENIED);

    UserRequest.Create registrationReq = new UserRequest.Create(user);
    UserResponse registrationRes = testSvc.call(UserResponse.class
    , StdPath.REGISTRATION_METHOD, StdPath.REGISTRATION, registrationReq);
    Supplier<String> registrationMsg
    = () -> "registrationRes=" + Json.serializep(om, registrationRes);
    assertThat(registrationRes.getStatus()).as(registrationMsg)
    .isEqualTo(BasicResponse.OK);
    assertThat(registrationRes.getData()).as(registrationMsg).isNotEmpty();
    Long userId = registrationRes.getData(0).getId();
    assertThat(userId).as(registrationMsg).isNotZero();

    UserRequest.Create authReq = new UserRequest.Create(user);
    AuthResponse authRes = testSvc.call(AuthResponse.class
    , StdPath.LOGIN_METHOD, StdPath.LOGIN, authReq);
    Supplier<String> authMsg = () -> "authRes=" + Json.serializep(om, authRes);
    assertThat(authRes.getStatus()).as(authMsg).isEqualTo(BasicResponse.OK);
    assertThat(authRes.getData()).as(authMsg).isNotNull().size().isEqualTo(2);
    String token = authRes.getData(AuthResponse.ACCESS_TOKEN_ID).token;
    String refreshToken = authRes.getData(AuthResponse.REFRESH_TOKEN_ID).token;

    AbstractRequest authenticatedReq = new AbstractRequest();
    authenticatedReq.token = token;
    AuthResponse authenticatedRes = testSvc.call(AuthResponse.class
    , StdPath.REFRESH_TOKEN_METHOD, StdPath.REFRESH_TOKEN, authenticatedReq);
    Supplier<String> authenticatedMsg
    = () -> "authenticatedRes=" + Json.serializep(om, authenticatedRes);
    assertThat(authenticatedRes.getStatus()).as(authenticatedMsg)
    .isEqualTo(BasicResponse.ACCESS_DENIED);

    authenticatedReq.token = refreshToken;
    AuthResponse authenticatedRes2 = testSvc.call(AuthResponse.class
    , StdPath.REFRESH_TOKEN_METHOD, StdPath.REFRESH_TOKEN, authenticatedReq);
    Supplier<String> authenticatedMsg2
    = () -> "authenticatedRes2=" + Json.serializep(om, authenticatedRes2);
    assertThat(authenticatedRes2.getStatus()).as(authenticatedMsg2)
    .isEqualTo(BasicResponse.OK);
    assertThat(authenticatedRes2.getData()).as(authenticatedMsg2)
    .isNotNull().size().isEqualTo(1);

    token = authenticatedRes2.getData(AuthResponse.ACCESS_TOKEN_ID).token;

    AbstractRequest logoutReq = new AbstractRequest();
    logoutReq.token = token;
    BasicResponse logoutRes = testSvc.call(BasicResponse.class
    , StdPath.LOGOUT_METHOD, StdPath.LOGOUT, logoutReq);
    Supplier<String> logoutMsg
    = () -> "logoutRes=" + Json.serializep(om, logoutRes);
    assertThat(logoutRes.getStatus()).as(logoutMsg).isEqualTo(BasicResponse.OK);

    AbstractRequest afterLogoutReq = new AbstractRequest();
    afterLogoutReq.token = token;
    BasicResponse afterLogoutRes = testSvc.call(BasicResponse.class
    , StdPath.LOGOUT_METHOD, StdPath.LOGOUT, afterLogoutReq);
    Supplier<String> afterLogoutMsg
    = () -> "afterLogoutRes=" + Json.serializep(om, afterLogoutRes);
    assertThat(afterLogoutRes.getStatus()).as(afterLogoutMsg)
    .isEqualTo(BasicResponse.ACCESS_DENIED);
  }

  @Test
  public void readNotAllowedType() throws Exception {
    UserGroupRequest.Read req = new UserGroupRequest.Read();
    req.filter.assignId(GROUP_ID);
    UserGroupResponse res = testSvc.call(UserGroupResponse.class
    , UserGroupRequest.READ_METHOD, UserGroupRequest.READ_PATH, req);
    Supplier<String> msg = () -> "res=" + Json.serializep(om, res);
    assertThat(res.getStatus()).as(msg).isEqualTo(BasicResponse.ACCESS_DENIED);

    req.token = authorize(ADMIN_DTO, testSvc, om);
    UserGroupResponse res2 = testSvc.call(UserGroupResponse.class
    , UserGroupRequest.READ_METHOD, UserGroupRequest.READ_PATH, req);
    Supplier<String> msg2 = () -> "res2=" + Json.serializep(om, res2);
    assertThat(res2.getStatus()).as(msg2).isEqualTo(BasicResponse.OK);
    assertThat(res2.getData().size()).as(msg2).isEqualTo(1);
    assertThat(res2.getData(0).getName()).as(msg2).isEqualTo(GROUP_NAME);
  }

  @Test
  public void readFieldWithNotEffectiveAllowedType() throws Exception {
    StateRequest.Read req = new StateRequest.Read();
    req.fields = list(StateDto.NAME, StateDto.FULLNAME, StateDto.COUNTRY);
    StateResponse res = testSvc.call(StateResponse.class
    , StateRequest.READ_METHOD, StateRequest.READ_PATH, req);
    Supplier<String> msg = () -> "res=" + Json.serializep(om, res);
    assertThat(res.getStatus()).as(msg).isEqualTo(BasicResponse.ACCESS_DENIED);

    req.token = authorize(ADMIN_DTO, testSvc, om);

    StateResponse res2 = testSvc.call(StateResponse.class
    , StateRequest.READ_METHOD, StateRequest.READ_PATH, req);
    Supplier<String> msg2 = () -> "res2=" + Json.serializep(om, res2);
    assertThat(res2.getStatus()).as(msg).isEqualTo(BasicResponse.ACCESS_DENIED);
    assertThat(res2.getDescr()).as(msg)
    .isEqualTo(i18n.message(SecurityMgr.Access_to_fields_denied
    , list(StateDto.COUNTRY).toString()
    ));
  }

  @Test
  public void readWithEmptyFields() throws Exception {
    UserRequest.Read req = new UserRequest.Read();
    req.filter.assignId(ADMIN_ID);
    UserResponse res = testSvc.call(UserResponse.class
    , UserRequest.READ_METHOD, UserRequest.READ_PATH, req);
    Supplier<String> msg = () -> "res=" + Json.serializep(om, res);
    assertThat(res.getStatus()).as(msg).isEqualTo(BasicResponse.ACCESS_DENIED);

    req.token = authorize(ADMIN_DTO, testSvc, om);
    UserResponse res2 = testSvc.call(UserResponse.class
    , UserRequest.READ_METHOD, UserRequest.READ_PATH, req);
    Supplier<String> msg2 = () -> "res2=" + Json.serializep(om, res2);
    assertThat(res2.getStatus()).as(msg2).isEqualTo(BasicResponse.OK);
    assertThat(res2.getData().size()).as(msg2).isEqualTo(1);
    assertThat(res2.getData(0).getGroups()).as(msg2).isNull();
    assertThat(res2.getData(0).getEmail()).as(msg2).isEqualTo(ADMIN_DTO.getEmail());
  }

  @Test
  public void readWithFields() throws Exception {
    UserRequest.Read req = new UserRequest.Read();
    req.filter.assignId(ADMIN_ID);
    req.fields = list(
      field(UserDto.GROUPS, UserGroupDto.ROLES)
    , UserDto.USERNAME
    , UserDto.EMAIL
    );
    UserResponse res = testSvc.call(UserResponse.class
    , UserRequest.READ_METHOD, UserRequest.READ_PATH, req);
    Supplier<String> msg = () -> "res=" + Json.serializep(om, res);
    assertThat(res.getStatus()).as(msg)
    .isEqualTo(BasicResponse.ACCESS_DENIED);

    req.token = authorize(ADMIN_DTO, testSvc, om);
    UserResponse res2 = testSvc.call(UserResponse.class
    , UserRequest.READ_METHOD, UserRequest.READ_PATH, req);
    Supplier<String> msg2 = () -> "res2=" + Json.serializep(om, res2);
    assertThat(res2.getStatus()).as(msg2).isEqualTo(BasicResponse.OK);
    assertThat(res2.getData().size()).as(msg2).isEqualTo(1);
    UserDto data2 = res2.getData(0);
    assertThat(data2.getEmail()).as(msg2).isEqualTo(ADMIN_DTO.getEmail());
    UserGroupDto role2 = data2.getGroups().iterator().next();
    assertThat(role2.getRoles()).as(UserGroupDto.ROLES + " " + msg2.get())
    .isNotNull().isNotEmpty();
    assertThat(role2.getRoles().iterator().next().getName())
    .as(UserGroupDto.ROLES + "[0].name " + msg2.get())
    .isNotBlank();
  }

  @Test
  public void readWithRelationFields() throws Exception {
    Set<String> EXPECTED_DENIED_FIELDS = new HashSet<>();
    Collections.addAll(EXPECTED_DENIED_FIELDS
    , PartnerDto.PARTNER_DETAILS
    , field(PartnerDto.USERS, UserDto.EMAIL)
    , field(PartnerDto.USERS, UserDto.GROUPS)
    );
    PartnerRequest.Read req = new PartnerRequest.Read();
    req.filter.assignId(PARTNER_ID);
    req.fields = new ArrayList<>(EXPECTED_DENIED_FIELDS);
    req.fields.add(PartnerDto.LOCATION_TYPE);
    PartnerResponse res = testSvc.call(PartnerResponse.class
    , PartnerRequest.READ_METHOD, PartnerRequest.READ_PATH, req);
    Supplier<String> msg = () -> "res=" + Json.serializep(om, res);
    assertThat(res.getStatus()).as(msg).isEqualTo(BasicResponse.ACCESS_DENIED);
    assertThat(res.getDescr()).as(msg)
    .isEqualTo(i18n.message(SecurityMgr.Access_to_fields_denied, EXPECTED_DENIED_FIELDS.toString()
    ));

    req.token = authorize(ADMIN_DTO, testSvc, om);
    PartnerResponse res2 = testSvc.call(PartnerResponse.class
    , PartnerRequest.READ_METHOD, PartnerRequest.READ_PATH, req);
    Supplier<String> msg2 = () -> "res2=" + Json.serializep(om, res2);
    assertThat(res2.getStatus()).as(msg2).isEqualTo(BasicResponse.OK);
    assertThat(res2.getData().size()).as(msg2).isEqualTo(1);
    PartnerDto partnerData = res2.getData(0);
    UserDto userData = partnerData.getUsers().iterator().next();
    assertThat(userData.getEmail()).as(msg2).isEqualTo(ADMIN_DTO.getEmail());
    UserGroupDto groupData = userData.getGroups().iterator().next();
    assertThat(groupData.getName()).as(msg2).isEqualTo(PARTNER_GROUP);
  }

  @Test
  public void update(CapturedOutput output) throws Exception {
    List<String> EXPECTED_SQL = list(
      "partnerName='" + PARTNER_NAME + "'"
    , "locationTypeId=NULL"
    , "update personDetails pdw1_0 set " + PersonDetailsRto.MARRIED + "=NULL"
    , "where p1_0.partnerRegdate>='2022-01-01T00:00:00.000"
    , "and p1_0.partnerRegdate<'2022-01-30T00:00:00.000"
    , "and p1_0.id=1"
    );
    PartnerRequest.Update req = new PartnerRequest.Update();

    req.fields = list(
      field(PartnerRto.PERSON_DETAILS, PersonDetailsRto.MARRIED)
    , PartnerRto.LOCATION_TYPE_ID
    );
    req.filter.assignId(PARTNER_ID);
    req.filter.setPartnerRegdate
    (new Range<>(LocalDate.of(2022, 1, 1), LocalDate.of(2022, 1, 30)));
    req.data = new PartnerRto();
    req.data.setPartnerName(PARTNER_NAME);
    PartnerResponse res = testSvc.call(PartnerResponse.class
    , PartnerRequest.UPDATE_METHOD, PartnerRequest.UPDATE_PATH, req);
    Supplier<String> msg = () -> "res=" + Json.serializep(om, res);
    assertThat(res.getStatus()).as(msg).isEqualTo(BasicResponse.ACCESS_DENIED);

    req.fields = list(
      field(PartnerDto.PERSON_DETAILS, PersonDetailsDto.MARRIED)
    , PartnerDto.LOCATION_TYPE
    );
    req.token = authorize(ADMIN_DTO, testSvc, om);
    PartnerResponse res2 = testSvc.call(PartnerResponse.class
    , PartnerRequest.UPDATE_METHOD, PartnerRequest.UPDATE_PATH, req);
    Supplier<String> msg2 = () -> "res2=" + Json.serializep(om, res2) + ", status:";
    assertThat(res2.getStatus()).as(msg2).isEqualTo(BasicResponse.CLIENT);
    assertThat(res2.getDescr()).as(msg2)
    .isEqualTo(i18n.message(SecurityMgr.Fields_contains_not_plain_column
    , AbstractRequest.FIELDS, PartnerDto.LOCATION_TYPE));
    assertThat(output).doesNotContain(EXPECTED_SQL);

    req.fields = list(
      field(PartnerRto.PERSON_DETAILS, PersonDetailsRto.MARRIED)
    , PartnerRto.LOCATION_TYPE_ID
    );
    PartnerResponse res3 = testSvc.call(PartnerResponse.class
    , PartnerRequest.UPDATE_METHOD, PartnerRequest.UPDATE_PATH, req);
    Supplier<String> msg3 = () -> "res3=" + Json.serializep(om, res3);
    assertThat(res3.getStatus()).as(msg3).isEqualTo(BasicResponse.OK);
    assertThat(output).contains(EXPECTED_SQL);
  }

  @Test
  public void createWithRestrictedClass() throws Exception {
    ContactDetailsRequest.Create req
    = new ContactDetailsRequest.Create(new ContactDetailsRto());
    req.data.setId(1L);
    req.data.setNote("note");
    ContactDetailsResponse res = testSvc.call(ContactDetailsResponse.class
    , ContactDetailsRequest.CREATE_METHOD, ContactDetailsRequest.CREATE_PATH, req);
    Supplier<String> msg = () -> "res=" + Json.serializep(om, res);
    assertThat(res.getStatus()).as(msg).isEqualTo(BasicResponse.ACCESS_DENIED);
  }

  @Test
  public void deleteWithRestrictedClass() throws Exception {
    ContactDetailsRequest.Delete req = new ContactDetailsRequest.Delete();
    req.filter.assignId(1L);
    ContactDetailsResponse res = testSvc.call(ContactDetailsResponse.class
    , ContactDetailsRequest.DELETE_METHOD, ContactDetailsRequest.DELETE_PATH, req);
    Supplier<String> msg = () -> "res=" + Json.serializep(om, res);
    assertThat(res.getStatus()).as(msg).isEqualTo(BasicResponse.ACCESS_DENIED);
  }

}
