package biz.softfor.util;

import biz.softfor.util.api.BasicResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class RequestUtil {

  public final static String JSON_CONTENT_TYPE = "application/json";

  public static String getBody(HttpServletRequest request) {
    String result;
    String encoding = request.getCharacterEncoding();
    if(encoding == null) {
      encoding = StandardCharsets.UTF_8.name();
    }
    try {
      result = new String(request.getInputStream().readAllBytes(), encoding);
    }
    catch(IOException ex) {
      result = "Read error from request: " + ex.getMessage();
    }
    return result;
  }

  public static void setRestRequestProperties(URLConnection connection) {
    connection.setRequestProperty("Accept-Charset", StandardCharsets.UTF_8.name());
    connection.setRequestProperty("Content-Type", JSON_CONTENT_TYPE
    + ";charset=" + StandardCharsets.UTF_8.name());
  }

  public static int toStatus(int httpStatus) {
    int result;
    result = switch(httpStatus) {
      case HttpURLConnection.HTTP_OK -> BasicResponse.OK;
      case HttpURLConnection.HTTP_BAD_REQUEST -> BasicResponse.BAD_REQUEST;
      case HttpURLConnection.HTTP_NOT_FOUND, HttpURLConnection.HTTP_BAD_METHOD -> BasicResponse.NOT_FOUND;
      case HttpURLConnection.HTTP_UNAUTHORIZED, HttpURLConnection.HTTP_FORBIDDEN -> BasicResponse.ACCESS_DENIED;
      default -> BasicResponse.SERVER_ERROR;
    };
    return result;
  }

}
