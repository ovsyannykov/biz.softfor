package biz.softfor.user.spring.rest;

import biz.softfor.spring.security.service.UrlRoleCalc;
import biz.softfor.testutil.spring.RestAssuredCall;
import biz.softfor.user.api.UserRequest;
import biz.softfor.user.api.UserResponse;
import biz.softfor.user.jpa.RoleRequest;
import biz.softfor.user.jpa.RoleWor;
import biz.softfor.user.jpa.User;
import biz.softfor.user.jpa.User_;
import biz.softfor.user.spring.RoleSvc;
import biz.softfor.user.spring.rest.testassets.HttpRequestsMgrUrlByClassTestCtlr;
import biz.softfor.user.spring.rest.testassets.HttpRequestsMgrUrlTestCtlr;
import biz.softfor.user.spring.rest.testassets.TeztEntity;
import biz.softfor.user.spring.rest.testassets.TeztEntityDto;
import biz.softfor.user.spring.rest.testassets.TeztEntityRequest;
import biz.softfor.user.spring.rest.testassets.TeztEntityResponse;
import biz.softfor.util.Json;
import biz.softfor.util.api.AbstractRequest;
import biz.softfor.util.api.BasicResponse;
import biz.softfor.util.api.CommonResponse;
import biz.softfor.util.api.StdPath;
import biz.softfor.util.security.ClassRoleCalc;
import biz.softfor.util.security.FieldRoleCalc;
import biz.softfor.util.security.DefaultAccess;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.lang.reflect.Field;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.extern.java.Log;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.list;
import static org.assertj.core.util.Sets.set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.bind.annotation.RequestMethod;

@SpringBootTest(classes = { ConfigUserRest.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
@EntityScan(basePackageClasses = { TeztEntity.class })
@Log
public class SecurityMgrUpdateTest {

  @PersistenceContext
  protected EntityManager em;

  @Autowired
  protected PlatformTransactionManager tm;

  @Autowired
  private RoleSvc roleSvc;

  @Autowired
  private ObjectMapper om;

  @LocalServerPort
  private int port;

  private RestAssuredCall testSvc;
  private Function<CommonResponse, Supplier<String>> msgr;

  @BeforeEach
  public void beforeEach() {
    RestAssured.basePath = StdPath.ROOT;
    RestAssured.defaultParser = Parser.JSON;
    RestAssured.port = port;
    testSvc = new RestAssuredCall(om);
    msgr = res -> (() -> "res=" + Json.serializep(om, res));
  }

  @Test
  public void addRemoveRole() throws Exception {
    Field everybodyField = TeztEntity.class.getDeclaredField(TeztEntityDto.EVERYBODY);
    long ROLE_ID = new FieldRoleCalc(TeztEntity.class, everybodyField).id();
    int GROUP_ID = 1;
    RoleWor data = new RoleWor();
    RoleRequest.Update roleReq = new RoleRequest.Update(data);
    roleReq.filter.assignId(ROLE_ID);
    TeztEntityRequest.Read req = new TeztEntityRequest.Read();
    req.filter.assignId(1);
    req.fields = list(TeztEntityDto.EVERYBODY);
    {
      TeztEntityResponse res = testSvc.call(TeztEntityResponse.class
      , TeztEntityRequest.METHOD, TeztEntityRequest.TEST_SECURITY + StdPath.READ
      , req);
      Supplier<String> msg = msgr.apply(res);
      assertThat(res.getStatus()).as(msg).isEqualTo(BasicResponse.OK);
      assertThat(res.getData().size()).as(msg).isEqualTo(1);
      assertThat(res.getData(0).getEverybody()).as(msg)
      .isEqualTo("everybody1");
    } {
      roleReq.data.setGroupIds(set(GROUP_ID));
      roleSvc.update(roleReq);
    } {
      TeztEntityResponse res = testSvc.call(TeztEntityResponse.class
      , TeztEntityRequest.METHOD, TeztEntityRequest.TEST_SECURITY + StdPath.READ
      , req);
      assertThat(res.getStatus()).as(msgr.apply(res))
      .isEqualTo(BasicResponse.ACCESS_DENIED);
    } {
      roleReq.data.setGroupIds(set());
      roleSvc.update(roleReq);
    } {
      TeztEntityResponse res = testSvc.call(TeztEntityResponse.class
      , TeztEntityRequest.METHOD, TeztEntityRequest.TEST_SECURITY + StdPath.READ
      , req);
      Supplier<String> msg = msgr.apply(res);
      assertThat(res.getStatus()).as(msg).isEqualTo(BasicResponse.OK);
      assertThat(res.getData().size()).as(msg).isEqualTo(1);
      assertThat(res.getData(0).getEverybody()).as(msg)
      .isEqualTo("everybody1");
    }
  }

  @Test
  public void disableEnable() throws Exception {
    long ACTION_ID = new FieldRoleCalc
    (User.class, User.class.getDeclaredField(User_.EMAIL)).id();
    RoleWor data = new RoleWor();
    RoleRequest.Update roleReq = new RoleRequest.Update(data);
    roleReq.filter.assignId(ACTION_ID);
    UserRequest.Read req = new UserRequest.Read();
    req.filter.assignId(SecurityTest.ADMIN_ID);
    req.fields = list(User_.EMAIL);
    {
      UserResponse res = testSvc.call(UserResponse.class
      , UserRequest.READ_METHOD, UserRequest.READ_PATH, req);
      assertThat(res.getStatus()).as(msgr.apply(res))
      .isEqualTo(BasicResponse.ACCESS_DENIED);
    } {
      roleReq.data.setDisabled(true);
      roleSvc.update(roleReq);
    } {
      UserResponse res = testSvc.call(UserResponse.class
      , UserRequest.READ_METHOD, UserRequest.READ_PATH, req);
      Supplier<String> msg = msgr.apply(res);
      assertThat(res.getStatus()).as(msg).isEqualTo(BasicResponse.OK);
      assertThat(res.getData().size()).as(msg).isEqualTo(1);
      assertThat(res.getData(0).getEmail()).as(msg)
      .isEqualTo(SecurityTest.ADMIN_DTO.getEmail());
    } {
      roleReq.data.setDisabled(false);
      roleSvc.update(roleReq);
    } {
      UserResponse res = testSvc.call(UserResponse.class
      , UserRequest.READ_METHOD, UserRequest.READ_PATH, req);
      assertThat(res.getStatus()).as(msgr.apply(res))
      .isEqualTo(BasicResponse.ACCESS_DENIED);
    }
  }

  @Test
  public void defaultAccess() throws Exception {
    Field everybodyField = TeztEntity.class.getDeclaredField(TeztEntityDto.EVERYBODY);
    long ROLE_ID = new FieldRoleCalc(TeztEntity.class, everybodyField).id();
    RoleWor data = new RoleWor();
    RoleRequest.Update roleReq = new RoleRequest.Update(data);
    roleReq.filter.assignId(ROLE_ID);
    TeztEntityRequest.Read req = new TeztEntityRequest.Read();
    req.filter.assignId(1);
    req.fields = list(TeztEntityDto.EVERYBODY);
    {
      TeztEntityResponse res = testSvc.call(TeztEntityResponse.class
      , TeztEntityRequest.METHOD, TeztEntityRequest.TEST_SECURITY + StdPath.READ
      , req);
      Supplier<String> msg = msgr.apply(res);
      assertThat(res.getStatus()).as(msg).isEqualTo(BasicResponse.OK);
      assertThat(res.getData().size()).as(msg).isEqualTo(1);
      assertThat(res.getData(0).getEverybody()).as(msg)
      .isEqualTo("everybody1");
    } {
      roleReq.data.setDefaultAccess(DefaultAccess.AUTHORIZED);
      roleSvc.update(roleReq);
    } {
      TeztEntityResponse res = testSvc.call(TeztEntityResponse.class
      , TeztEntityRequest.METHOD, TeztEntityRequest.TEST_SECURITY + StdPath.READ
      , req);
      assertThat(res.getStatus()).as(msgr.apply(res))
      .isEqualTo(BasicResponse.ACCESS_DENIED);
    } {
      roleReq.data.setDefaultAccess(DefaultAccess.EVERYBODY);
      roleSvc.update(roleReq);
    } {
      TeztEntityResponse res = testSvc.call(TeztEntityResponse.class
      , TeztEntityRequest.METHOD, TeztEntityRequest.TEST_SECURITY + StdPath.READ
      , req);
      Supplier<String> msg = msgr.apply(res);
      assertThat(res.getStatus()).as(msg).isEqualTo(BasicResponse.OK);
      assertThat(res.getData().size()).as(msg).isEqualTo(1);
      assertThat(res.getData(0).getEverybody()).as(msg)
      .isEqualTo("everybody1");
    }
  }

  @Test
  public void url() throws Exception {
    String HTTP_METHOD = RequestMethod.GET.name();
    String ENDPOINT = TeztEntityRequest.TEST_SECURITY
    + HttpRequestsMgrUrlTestCtlr.DEFAULT_ACCESS;
    long ACTION_ID = new UrlRoleCalc(
      HttpRequestsMgrUrlTestCtlr.class
    , HttpRequestsMgrUrlTestCtlr.class.getMethod
      (HttpRequestsMgrUrlTestCtlr.DEFAULT_ACCESS)
    ).id();
    RoleWor data = new RoleWor();
    RoleRequest.Update roleReq = new RoleRequest.Update(data);
    roleReq.filter.assignId(ACTION_ID);
    AbstractRequest req = new AbstractRequest();
    {
      CommonResponse res = testSvc.call(CommonResponse.class
      , HTTP_METHOD, ENDPOINT, req);
      Supplier<String> msg = msgr.apply(res);
      assertThat(res.getStatus()).as(msg).isEqualTo(BasicResponse.OK);
    } {
      roleReq.data.setDefaultAccess(DefaultAccess.AUTHORIZED);
      roleSvc.update(roleReq);
    } {
      CommonResponse res = testSvc.call(CommonResponse.class
      , HTTP_METHOD, ENDPOINT, req);
      assertThat(res.getStatus()).as(msgr.apply(res))
      .isEqualTo(BasicResponse.ACCESS_DENIED);
    } {
      roleReq.data.setDefaultAccess(DefaultAccess.EVERYBODY);
      roleSvc.update(roleReq);
    } {
      CommonResponse res = testSvc.call(CommonResponse.class
      , HTTP_METHOD, ENDPOINT, req);
      Supplier<String> msg = msgr.apply(res);
      assertThat(res.getStatus()).as(msg).isEqualTo(BasicResponse.OK);
    }
  }

  @Test
  public void urlByClass() throws Exception {
    long ACTION_ID = new ClassRoleCalc(HttpRequestsMgrUrlByClassTestCtlr.class).id();
    RoleWor data = new RoleWor();
    RoleRequest.Update roleReq = new RoleRequest.Update(data);
    roleReq.filter.assignId(ACTION_ID);
    String HTTP_METHOD = RequestMethod.GET.name();
    String ENDPOINT = TeztEntityRequest.TEST_SECURITY
    + HttpRequestsMgrUrlByClassTestCtlr.URL_BY_CLASS;
    AbstractRequest req = new AbstractRequest();
    {
      CommonResponse res = testSvc.call(CommonResponse.class
      , HTTP_METHOD, ENDPOINT, req);
      Supplier<String> msg = msgr.apply(res);
      assertThat(res.getStatus()).as(msg).isEqualTo(BasicResponse.OK);
    } {
      roleReq.data.setDefaultAccess(DefaultAccess.AUTHORIZED);
      roleSvc.update(roleReq);
    } {
      CommonResponse res = testSvc.call(CommonResponse.class
      , HTTP_METHOD, ENDPOINT, req);
      assertThat(res.getStatus()).as(msgr.apply(res))
      .isEqualTo(BasicResponse.ACCESS_DENIED);
    } {
      roleReq.data.setDefaultAccess(DefaultAccess.EVERYBODY);
      roleSvc.update(roleReq);
    } {
      CommonResponse res = testSvc.call(CommonResponse.class
      , HTTP_METHOD, ENDPOINT, req);
      Supplier<String> msg = msgr.apply(res);
      assertThat(res.getStatus()).as(msg).isEqualTo(BasicResponse.OK);
    }
  }

}
