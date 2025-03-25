package biz.softfor.util.api;

import biz.softfor.util.AbstractError;
import biz.softfor.util.HttpStatusCode;
import biz.softfor.util.RequestUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;

@AllArgsConstructor(onConstructor = @__(@JsonCreator))
@ToString
@Log
public class ErrorData {

  public final int httpStatus;
  public final String httpMethod;
  public final String ip;
  public final String url;
  public final int size;
  public final String body;
  public final Map<String, String[]> parameters;
  public final String stackTrace;

  public final static String MESSAGE_KEY = "message";

  @JsonIgnore
  private final Exception ex;

  private static String[] secretKeys = { "pass", "pwd", "secr" };

  public static String[] getSecretKeys() {
    return secretKeys;
  }

  public static void setSecretKeys(String[] keys) {
    secretKeys = keys;
  }

  private final static String BAD_REQUEST_EXCEPTION
  = "org.springframework.http.converter.HttpMessageNotReadableException";
  private final static String NO_RESOURCE_FOUND_EXCEPTION
  = "org.springframework.web.servlet.resource.NoResourceFoundException";

  public ErrorData(Exception ex, HttpServletRequest request) {
    this.ex = ex;
    if(ex == null) {
      Integer httpStatusCode
      = (Integer)request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
      httpStatus = httpStatusCode == null
      ? HttpURLConnection.HTTP_INTERNAL_ERROR : httpStatusCode;
    } else if(ex instanceof ServerError) {
      httpStatus = HttpURLConnection.HTTP_INTERNAL_ERROR;
    } else if(ex instanceof ClientError) {
      httpStatus = HttpURLConnection.HTTP_BAD_REQUEST;
    } else {
      httpStatus = switch(ex.getClass().getName()) {
        case BAD_REQUEST_EXCEPTION -> HttpURLConnection.HTTP_BAD_REQUEST;
        case NO_RESOURCE_FOUND_EXCEPTION -> HttpURLConnection.HTTP_NOT_FOUND;
        default -> {
          Integer httpStatusCode
          = (Integer)request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
          yield httpStatusCode == null
          ? HttpURLConnection.HTTP_INTERNAL_ERROR : httpStatusCode;
        }
      };
    }
    stackTrace = AbstractError.stackTraceToString(ex);
    if(ex != null) {
      log.info(stackTrace);
    }
    httpMethod = request.getMethod();
    String xForwardedFor = request.getHeader("X-FORWARDED-FOR");
    if(StringUtils.isBlank(xForwardedFor)) {
      ip = request.getRemoteAddr();
      url = request.getRequestURI();
    } else {
      ip = xForwardedFor;
      url = (String)request.getAttribute(RequestDispatcher.FORWARD_SERVLET_PATH);
    }
    size = request.getContentLength();
    body = size > 0 ? RequestUtil.getBody(request) : null;
    Map<String, String[]> parameterMap = request.getParameterMap();
    if(parameterMap == null) {
      parameters = null;
    } else {
      parameters = new HashMap<>(parameterMap.size());
      parameterMap.forEach((k, v) -> {
        if(!StringUtils.containsAnyIgnoreCase(k, secretKeys)) {
          parameters.put(k, v);
        }
      });
    }
  }

  public int status() {
    int status;
    if(ex == null) {
      status = RequestUtil.toStatus(httpStatus);
    } else if(ex instanceof AbstractError error) {
      status = error.code;
    } else {
      status = switch(ex.getClass().getName()) {
        case BAD_REQUEST_EXCEPTION -> BasicResponse.REQUEST_PARSE;
        case NO_RESOURCE_FOUND_EXCEPTION -> BasicResponse.NOT_FOUND;
        default -> RequestUtil.toStatus(httpStatus);
      };
    }
    return status;
  }

  public String message() {
    return ex == null
    ? HttpStatusCode.getByValue(httpStatus).getDescription()
    : ex.getMessage();
  }

  public String toJson(ObjectMapper objectMapper) {
    String result;
    try {
      result = objectMapper.writeValueAsString(this);
    }
    catch(JsonProcessingException ex) {
      result = "{\"" + MESSAGE_KEY + "\":\""
      + ex.getMessage().replace('"', '\'') + "\"}";
    }
    return result;
  }

}
