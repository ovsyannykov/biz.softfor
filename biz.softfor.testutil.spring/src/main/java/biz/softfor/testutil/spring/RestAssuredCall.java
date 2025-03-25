package biz.softfor.testutil.spring;

import biz.softfor.util.ServiceCall;
import biz.softfor.util.api.ServiceResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.java.Log;

@Log
public class RestAssuredCall extends ServiceCall {

  public RestAssuredCall(ObjectMapper objectMapper) {
    super("", 0, objectMapper);
  }

  @Override
  public ServiceResponse callWithStrBody
  (String httpMethod, String url, String params) throws Exception {
    RequestSpecification reqspec = RestAssured.with()
    .contentType(ContentType.JSON).headers(headers);
    Response response = reqspec.body(params).when().request(httpMethod, url);
    log.info(() -> httpMethod + "('" + url + "', " + params + ")="
    + response.asPrettyString() + "\nhttpStatus=" + response.getStatusCode());
    return new ServiceResponse(response.statusCode(), response.asString());
  }

}
