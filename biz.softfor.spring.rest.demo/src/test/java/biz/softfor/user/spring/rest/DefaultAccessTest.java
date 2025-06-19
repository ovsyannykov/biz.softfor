package biz.softfor.user.spring.rest;

import biz.softfor.i18nspring.I18n;
import biz.softfor.testutil.spring.RestAssuredCall;
import biz.softfor.user.api.UserDto;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.user.spring.rest.testassets.TeztEntity;
import biz.softfor.user.spring.rest.testassets.TeztEntityDto;
import biz.softfor.user.spring.rest.testassets.TeztEntityRequest;
import biz.softfor.user.spring.rest.testassets.TeztEntityResponse;
import biz.softfor.user.spring.rest.testassets.TeztEntityWor;
import biz.softfor.util.Json;
import biz.softfor.util.api.AbstractRequest;
import biz.softfor.util.api.BasicResponse;
import biz.softfor.util.api.CommonResponse;
import biz.softfor.util.api.StdPath;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import java.text.MessageFormat;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestMethod;

@SpringBootTest(classes = { ConfigUserRest.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
@EntityScan(basePackageClasses = { TeztEntity.class })
@ExtendWith(OutputCaptureExtension.class)
@Log
public class DefaultAccessTest {

  @Autowired
  private ObjectMapper om;

  @Autowired
  private I18n i18n;

  @LocalServerPort
  private int port;

  private RestAssuredCall testSvc;

  private final static String CREATE
  = TeztEntityRequest.TEST_SECURITY + StdPath.CREATE;
  private final static String DATA_DEFAULT_ACCESS
  = TeztEntityRequest.TEST_SECURITY + TeztEntityRequest.DATA_DEFAULT_ACCESS;
  private final static String DATA_DEFAULT_UPDATE_ACCESS
  = TeztEntityRequest.TEST_SECURITY + TeztEntityRequest.DATA_DEFAULT_UPDATE_ACCESS;

  public final static UserDto DEFAULT_ACCESS_USER_DTO = new UserDto();
  static {
    DEFAULT_ACCESS_USER_DTO.setUsername("testDefaultAccess");
    DEFAULT_ACCESS_USER_DTO.setPassword("12345678");
    DEFAULT_ACCESS_USER_DTO.setEmail("testDefaultAccess@t.co");
  };

  private final static TeztEntityWor TEST_ENTITY_WOR = new TeztEntityWor();
  static {
    TEST_ENTITY_WOR.setEverybody("everybody");
    TEST_ENTITY_WOR.setAuthorized("authorized");
    TEST_ENTITY_WOR.setNobody("nobody");
  };
  private final static TeztEntityRequest.Create CREATE_REQ
  = new TeztEntityRequest.Create(TEST_ENTITY_WOR);
  private final static List<String> ALL_FIELDS = list
  (TeztEntityDto.NOBODY, TeztEntityDto.AUTHORIZED, TeztEntityDto.EVERYBODY);

  @BeforeEach
  public void beforeEach() {
    RestAssured.basePath = StdPath.ROOT;
    RestAssured.defaultParser = Parser.JSON;
    RestAssured.port = port;
    testSvc = new RestAssuredCall(om);
  }

  @Test
  public void dataDefaultAccess() throws Exception {
    Integer id = createTestData();
    TeztEntityRequest.Read req = new TeztEntityRequest.Read();
    req.filter.assignId(id);

    req.fields = list(TeztEntityDto.EVERYBODY);
    TeztEntityResponse res = testSvc.call(TeztEntityResponse.class
    , RequestMethod.POST.name(), DATA_DEFAULT_ACCESS, req);
    Supplier<String> msg = () -> "res=" + Json.serializep(om, res);
    assertThat(res.getStatus()).as(msg).isEqualTo(BasicResponse.OK);
    assertThat(res.getData(0).getEverybody()).as(msg)
    .isEqualTo(TEST_ENTITY_WOR.getEverybody());

    req.fields = ALL_FIELDS;
    TeztEntityResponse res2 = testSvc.call(TeztEntityResponse.class
    , RequestMethod.POST.name(), DATA_DEFAULT_ACCESS, req);
    Supplier<String> msg2 = () -> "res2=" + Json.serializep(om, res2);
    assertThat(res2.getStatus()).as(msg2).isEqualTo(BasicResponse.ACCESS_DENIED);
    assertThat(res2.getDescr()).as(msg2)
    .isEqualTo(i18n.message(SecurityMgr.Access_to_fields_denied
    , list(TeztEntityDto.AUTHORIZED, TeztEntityDto.NOBODY).toString()
    ));
  }

  @Test
  public void dataDefaultAccessAuthorized() throws Exception {
    Integer id = createTestData();
    TeztEntityRequest.Read req = new TeztEntityRequest.Read();
    req.filter.assignId(id);
    req.fields = list(TeztEntityDto.AUTHORIZED, TeztEntityDto.EVERYBODY);
    TeztEntityResponse res = testSvc.call(TeztEntityResponse.class
    , RequestMethod.POST.name(), DATA_DEFAULT_ACCESS, req);
    Supplier<String> msg = () -> "res=" + Json.serializep(om, res);
    assertThat(res.getStatus()).as(msg).isEqualTo(BasicResponse.ACCESS_DENIED);
    assertThat(res.getDescr()).as(msg)
    .isEqualTo(i18n.message(SecurityMgr.Access_to_fields_denied
    , list(TeztEntityDto.AUTHORIZED).toString()
    ));

    req.token = SecurityTest.authorize(DEFAULT_ACCESS_USER_DTO, testSvc, om);
    TeztEntityResponse res2 = testSvc.call(TeztEntityResponse.class
    , RequestMethod.POST.name(), DATA_DEFAULT_ACCESS, req);
    Supplier<String> msg2 = () -> "res2=" + Json.serializep(om, res2);
    assertThat(res2.getStatus()).as(msg2).isEqualTo(BasicResponse.OK);
    assertThat(res2.getData(0).getAuthorized()).as(msg2)
    .isEqualTo(TEST_ENTITY_WOR.getAuthorized());

    req.fields = ALL_FIELDS;
    TeztEntityResponse res3 = testSvc.call(TeztEntityResponse.class
    , RequestMethod.POST.name(), DATA_DEFAULT_ACCESS, req);
    Supplier<String> msg3 = () -> "res3=" + Json.serializep(om, res3);
    assertThat(res3.getStatus()).as(msg3).isEqualTo(BasicResponse.ACCESS_DENIED);
    assertThat(res3.getDescr()).as(msg3)
    .isEqualTo(i18n.message(SecurityMgr.Access_to_fields_denied
    , list(TeztEntityDto.NOBODY).toString()
    ));
  }

  @Test
  public void dataDefaultAccessNobody() throws Exception {
    Integer id = createTestData();
    TeztEntityRequest.Read req = new TeztEntityRequest.Read();
    req.filter.assignId(id);
    req.fields = ALL_FIELDS;

    TeztEntityResponse res = testSvc.call(TeztEntityResponse.class
    , RequestMethod.POST.name(), DATA_DEFAULT_ACCESS, req);
    Supplier<String> msg = () -> "res=" + Json.serializep(om, res);
    assertThat(res.getStatus()).as(msg).isEqualTo(BasicResponse.ACCESS_DENIED);
    assertThat(res.getDescr()).as(msg)
    .isEqualTo(i18n.message(SecurityMgr.Access_to_fields_denied
    , list(TeztEntityDto.AUTHORIZED, TeztEntityDto.NOBODY).toString()
    ));

    req.token = SecurityTest.authorize(DEFAULT_ACCESS_USER_DTO, testSvc, om);
    TeztEntityResponse res2 = testSvc.call(TeztEntityResponse.class
    , RequestMethod.POST.name(), DATA_DEFAULT_ACCESS, req);
    Supplier<String> msg2 = () -> "res2=" + Json.serializep(om, res2);
    assertThat(res2.getStatus()).as(msg2).isEqualTo(BasicResponse.ACCESS_DENIED);
    assertThat(res2.getDescr()).as(msg2)
    .isEqualTo(i18n.message(SecurityMgr.Access_to_fields_denied
    , list(TeztEntityDto.NOBODY).toString()
    ));
  }

  @Test
  public void dataDefaultUpdateAccess(CapturedOutput output) throws Exception {
    Integer id = createTestData();
    String EXPECTED_SQL = "update testentities tew1_0 set nobody=NULL where tew1_0.id=" + id;
    TeztEntityRequest.Update req = new TeztEntityRequest.Update();
    req.filter.assignId(id);

    req.fields = list(TeztEntityDto.NOBODY);//defaultUpdateAccess = EVERYBODY
    TeztEntityResponse res = testSvc.call(TeztEntityResponse.class
    , RequestMethod.POST.name(), DATA_DEFAULT_UPDATE_ACCESS, req);
    Supplier<String> msg = () -> "res=" + Json.serializep(om, res);
    assertThat(res.getStatus()).as(msg).isEqualTo(BasicResponse.OK);
    assertThat(output).contains(EXPECTED_SQL);

    req.fields = ALL_FIELDS;
    TeztEntityResponse res2 = testSvc.call(TeztEntityResponse.class
    , RequestMethod.POST.name(), DATA_DEFAULT_UPDATE_ACCESS, req);
    Supplier<String> msg2 = () -> "res2=" + Json.serializep(om, res2);
    assertThat(res2.getStatus()).as(msg2).isEqualTo(BasicResponse.ACCESS_DENIED);
    assertThat(res2.getDescr()).as(msg2)
    .isEqualTo(i18n.message(SecurityMgr.Access_to_fields_denied
    , list(TeztEntityDto.EVERYBODY, TeztEntityDto.AUTHORIZED).toString()
    ));
  }

  @Test
  public void dataDefaultUpdateAccessAuthorized(CapturedOutput output)
  throws Exception {
    Integer id = createTestData();
    String EXPECTED_SQL = "update testentities tew1_0 set everybody=NULL,nobody=NULL where tew1_0.id=" + id;
    TeztEntityRequest.Update req = new TeztEntityRequest.Update();
    req.filter.assignId(id);

    //defaultUpdateAccess <= AUTHORIZED
    req.fields = list(TeztEntityDto.EVERYBODY, TeztEntityDto.NOBODY);
    TeztEntityResponse res = testSvc.call(TeztEntityResponse.class
    , RequestMethod.POST.name(), DATA_DEFAULT_UPDATE_ACCESS, req);
    Supplier<String> msg = () -> "res=" + Json.serializep(om, res);
    assertThat(res.getStatus()).as(msg).isEqualTo(BasicResponse.ACCESS_DENIED);
    assertThat(res.getDescr()).as(msg)
    .isEqualTo(i18n.message(SecurityMgr.Access_to_fields_denied
    , list(TeztEntityDto.EVERYBODY).toString()
    ));

    req.token = SecurityTest.authorize(DEFAULT_ACCESS_USER_DTO, testSvc, om);
    TeztEntityResponse res2 = testSvc.call(TeztEntityResponse.class
    , RequestMethod.POST.name(), DATA_DEFAULT_UPDATE_ACCESS, req);
    Supplier<String> msg2 = () -> "res2=" + Json.serializep(om, res2);
    assertThat(res2.getStatus()).as(msg2).isEqualTo(BasicResponse.OK);
    assertThat(output).contains(EXPECTED_SQL);

    req.fields = ALL_FIELDS;
    TeztEntityResponse res3 = testSvc.call(TeztEntityResponse.class
    , RequestMethod.POST.name(), DATA_DEFAULT_UPDATE_ACCESS, req);
    Supplier<String> msg3 = () -> "res3=" + Json.serializep(om, res3);
    assertThat(res3.getStatus()).as(msg3).isEqualTo(BasicResponse.ACCESS_DENIED);
    assertThat(res3.getDescr()).as(msg3)
    .isEqualTo(i18n.message(SecurityMgr.Access_to_fields_denied
    , list(TeztEntityDto.AUTHORIZED).toString()
    ));
  }

  @Test
  public void dataDefaultUpdateAccessNobody()
  throws Exception {
    Integer id = createTestData();
    TeztEntityRequest.Update req = new TeztEntityRequest.Update();
    req.filter.assignId(id);

    //defaultUpdateAccess <= NOBODY
    req.fields = ALL_FIELDS;
    TeztEntityResponse res = testSvc.call(TeztEntityResponse.class
    , RequestMethod.POST.name(), DATA_DEFAULT_UPDATE_ACCESS, req);
    Supplier<String> msg = () -> "res=" + Json.serializep(om, res);
    assertThat(res.getStatus()).as(msg).isEqualTo(BasicResponse.ACCESS_DENIED);
    assertThat(res.getDescr()).as(msg)
    .isEqualTo(i18n.message(SecurityMgr.Access_to_fields_denied
    , list(TeztEntityDto.EVERYBODY, TeztEntityDto.AUTHORIZED).toString()
    ));

    req.token = SecurityTest.authorize(DEFAULT_ACCESS_USER_DTO, testSvc, om);
    TeztEntityResponse res2 = testSvc.call(TeztEntityResponse.class
    , RequestMethod.POST.name(), DATA_DEFAULT_UPDATE_ACCESS, req);
    Supplier<String> msg2 = () -> "res2=" + Json.serializep(om, res2);
    assertThat(res2.getStatus()).as(msg2).isEqualTo(BasicResponse.ACCESS_DENIED);
    assertThat(res2.getDescr()).as(msg2)
    .isEqualTo(i18n.message(SecurityMgr.Access_to_fields_denied
    , list(TeztEntityDto.AUTHORIZED).toString()
    ));
  }

  @Test
  public void urlDefaultAccessEverybody() throws Exception {
    AbstractRequest req = new AbstractRequest();
    CommonResponse res = testSvc.call(CommonResponse.class
    , RequestMethod.POST.name()
    , TeztEntityRequest.TEST_SECURITY
      + TeztEntityRequest.URL_DEFAULT_ACCESS_EVERYBODY
    , req
    );
    Supplier<String> msg = () -> "res=" + Json.serializep(om, res);
    assertThat(res.getStatus()).as(msg).isEqualTo(BasicResponse.OK);
  }

  @Test
  public void urlDefaultAccessAuthorized() throws Exception {
    AbstractRequest req = new AbstractRequest();
    CommonResponse res = testSvc.call(CommonResponse.class
    , RequestMethod.POST.name()
    , TeztEntityRequest.TEST_SECURITY
      + TeztEntityRequest.URL_DEFAULT_ACCESS_AUTHORIZED
    , req
    );
    Supplier<String> msg = () -> "res=" + Json.serializep(om, res);
    assertThat(res.getStatus()).as(msg).isEqualTo(BasicResponse.ACCESS_DENIED);

    req.token = SecurityTest.authorize(DEFAULT_ACCESS_USER_DTO, testSvc, om);
    CommonResponse res2 = testSvc.call(CommonResponse.class
    , RequestMethod.POST.name()
    , TeztEntityRequest.TEST_SECURITY
      + TeztEntityRequest.URL_DEFAULT_ACCESS_AUTHORIZED
    , req
    );
    Supplier<String> msg2 = () -> "res2=" + Json.serializep(om, res2);
    assertThat(res2.getStatus()).as(msg2).isEqualTo(BasicResponse.OK);
  }

  @Test
  public void urlDefaultAccessNobody() throws Exception {
    AbstractRequest req = new AbstractRequest();
    CommonResponse res = testSvc.call(CommonResponse.class
    , RequestMethod.POST.name()
    , TeztEntityRequest.TEST_SECURITY
      + TeztEntityRequest.URL_DEFAULT_ACCESS_NOBODY
    , req
    );
    Supplier<String> msg = () -> "res=" + Json.serializep(om, res);
    assertThat(res.getStatus()).as(msg).isEqualTo(BasicResponse.ACCESS_DENIED);

    req.token = SecurityTest.authorize(DEFAULT_ACCESS_USER_DTO, testSvc, om);
    CommonResponse res2 = testSvc.call(CommonResponse.class
    , RequestMethod.POST.name()
    , TeztEntityRequest.TEST_SECURITY
      + TeztEntityRequest.URL_DEFAULT_ACCESS_NOBODY
    , req
    );
    Supplier<String> msg2 = () -> "res2=" + Json.serializep(om, res2);
    assertThat(res2.getStatus()).as(msg2).isEqualTo(BasicResponse.ACCESS_DENIED);
  }

  private Integer createTestData() throws Exception {
    return testSvc.call(TeztEntityResponse.class
    , RequestMethod.POST.name(), CREATE, CREATE_REQ).getData(0).getId();
  }

}
