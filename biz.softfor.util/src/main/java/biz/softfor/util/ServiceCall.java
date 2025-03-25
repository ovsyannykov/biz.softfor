package biz.softfor.util;

import biz.softfor.util.api.BasicResponse;
import biz.softfor.util.api.ErrorData;
import biz.softfor.util.api.ServiceResponse;
import biz.softfor.util.api.StdPath;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public abstract class ServiceCall {

  public final Map<String, String> headers;

  private final String rootPath;
  protected final int timeout;//ms
  private final ObjectMapper objectMapper;

  public ServiceCall(String rootPath, int timeout, ObjectMapper objectMapper) {
    this.rootPath = rootPath;
    this.timeout = timeout;
    this.objectMapper = objectMapper;
    headers = new HashMap<>();
  }

  abstract public ServiceResponse callWithStrBody
  (String url, String httpMethod, String params) throws Exception;

  public <R extends BasicResponse> R callWithStrBody(
    Class<R> resultClass
  , String httpMethod
  , String url
  , String params
  ) throws Exception {
    R result;
    ServiceResponse response = callWithStrBody(httpMethod, url, params);
    if(StringUtils.isBlank(response.body)) {
      result = Reflection.newInstance(resultClass);
      int resStatus = response.status;
      result.setStatus(RequestUtil.toStatus(resStatus));
      result.setDescr(HttpStatusCode.getByValue(resStatus).getDescription());
    } else {
      try {
        result = objectMapper.readValue(response.body, resultClass);
      }
      catch(JsonProcessingException ex) {
        ErrorData errorData = new ErrorData(
          response.status
        , httpMethod
        , StdPath.LOCALHOST
        , url
        , response.body.length()
        , response.body
        , null
        , AbstractError.stackTraceToString(ex)
        , ex
        );
        result = Reflection.newInstance(resultClass);
        result.setStatus(BasicResponse.RESPONSE_PARSE);
        result.setDescr(BasicResponse.Response_parse_error);
        result.setErrorData(errorData);
      }
    }
    return result;
  }

  public <R extends BasicResponse> R call(
    Class<R> resultClass
  , String httpMethod
  , String url
  , Object params
  ) throws Exception {
    String body = objectMapper.writeValueAsString(params);
    return callWithStrBody(resultClass, httpMethod, rootPath + url, body);
  }

}
