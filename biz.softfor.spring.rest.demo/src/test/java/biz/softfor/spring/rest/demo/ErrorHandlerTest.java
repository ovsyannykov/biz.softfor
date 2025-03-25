package biz.softfor.spring.rest.demo;

import biz.softfor.partner.api.AppointmentRequest;
import biz.softfor.partner.api.AppointmentResponse;
import biz.softfor.testutil.Check;
import biz.softfor.testutil.spring.RestAssuredCall;
import biz.softfor.user.api.UserGroupRequest;
import biz.softfor.user.api.UserGroupResponse;
import biz.softfor.util.Json;
import biz.softfor.util.api.BasicResponse;
import biz.softfor.util.api.DeleteRequest;
import biz.softfor.util.api.Identifiable;
import biz.softfor.util.api.StdPath;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import lombok.extern.java.Log;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Log
public class ErrorHandlerTest {

  @LocalServerPort
  private int port;

  @Autowired
  private Check check;

  @Autowired
  private ObjectMapper om;

  private RestAssuredCall testSvc;

  @BeforeEach
  public void beforeEach() {
    testSvc = new RestAssuredCall(om);
    RestAssured.basePath = StdPath.ROOT;
    RestAssured.defaultParser = Parser.JSON;
    RestAssured.port = port;
  }

  @Test
  public void badFieldName() throws Exception {
    AppointmentRequest.Read request = new AppointmentRequest.Read();
    request.filter.assignId((short)1);
    Map<String, Object> requestMap
    = om.convertValue(request, Json.MAP_STRING_OBJECT_TYPEREF);
    Map filter = (Map)requestMap.get(DeleteRequest.FILTER);
    requestMap.remove(DeleteRequest.FILTER);
    requestMap.put(DeleteRequest.FILTER + "r", filter);
    AppointmentResponse response = testSvc.call(AppointmentResponse.class
    , AppointmentRequest.READ_METHOD, AppointmentRequest.READ_PATH, requestMap);
    check.isOk("response", response);
  }

  @Test
  public void badFieldDataType() throws Exception {
    UserGroupRequest.Read request = new UserGroupRequest.Read();
    request.filter.assignId(1);
    Map<String, Object> requestMap
    = om.convertValue(request, Json.MAP_STRING_OBJECT_TYPEREF);
    ((List)((Map)requestMap.get(DeleteRequest.FILTER)).get(Identifiable.ID))
    .add("badId");
    UserGroupResponse response = testSvc.call(UserGroupResponse.class
    , UserGroupRequest.READ_METHOD, UserGroupRequest.READ_PATH, requestMap);
    Supplier<String> responseMsg
    = () -> "response=" + Json.serializep(om, response);
    assertThat(response.getStatus()).as(responseMsg)
    .isEqualTo(BasicResponse.REQUEST_PARSE);
    assertThat(response.getDescr()).as(responseMsg)
    .isEqualTo("JSON parse error: Cannot deserialize value of type "
    + "`java.lang.Integer` from String \"badId\": not a valid "
    + "`java.lang.Integer` value");
  }

  @Test
  public void badJson() throws Exception {
    UserGroupRequest.Read request = new UserGroupRequest.Read();
    request.filter.assignId(1);
    String requestStr = Json.serialize(om, request)
    .replace("\"" + DeleteRequest.FILTER + "\"", DeleteRequest.FILTER);
    UserGroupResponse response = testSvc.callWithStrBody(UserGroupResponse.class
    , UserGroupRequest.READ_METHOD, UserGroupRequest.READ_PATH, requestStr);
    assertThat(response.getStatus())
    .as("response=" + Json.serializep(om, response))
    .isEqualTo(BasicResponse.REQUEST_PARSE);
    assertThat(response.getDescr())
    .as(() -> "response.descr=" + response.getDescr())
    .contains("Unexpected character ('f' (code 102)): was expecting double");
  }

  @Test
  public void badJsonWithEqualSymbols() throws Exception {
    UserGroupRequest.Read request = new UserGroupRequest.Read();
    request.filter.assignId(1);
    String requestStr = Json.serialize(om, request).replace("\":", "\"=");
    UserGroupResponse response = testSvc.callWithStrBody(UserGroupResponse.class
    , UserGroupRequest.READ_METHOD, UserGroupRequest.READ_PATH, requestStr);
    Supplier<String> responseMsg
    = () -> "response=" + Json.serializep(om, response);
    assertThat(response.getStatus()).as(responseMsg)
    .isEqualTo(BasicResponse.REQUEST_PARSE);
    assertThat(response.getDescr()).as(responseMsg)
    .contains("Unexpected character ('=' (code 61)): was expecting a colon to"
    + " separate field name and value");
  }

  @Test
  public void invalidPath() throws Exception {
    UserGroupRequest.Read req = new UserGroupRequest.Read();
    req.filter.assignId(1);
    UserGroupResponse res = testSvc.call
    (UserGroupResponse.class, UserGroupRequest.READ_METHOD, "/invalidPath", req);
    Supplier<String> msg = () -> "res=" + Json.serializep(om, res);
    assertThat(res.getStatus()).as(msg).isEqualTo(BasicResponse.NOT_FOUND);
  }

  @Test
  public void emptyBody() throws Exception {
    UserGroupResponse res = testSvc.call(UserGroupResponse.class
    , UserGroupRequest.READ_METHOD, UserGroupRequest.READ_PATH, "");
    Supplier<String> msg = () -> "response=" + Json.serializep(om, res);
    assertThat(res.getStatus()).as(msg).isEqualTo(BasicResponse.REQUEST_PARSE);
    assertThat(res.getDescr()).as(msg).contains("Cannot coerce empty String");
  }

}
