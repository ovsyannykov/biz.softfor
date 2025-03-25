package biz.softfor.util;

import biz.softfor.util.api.ServiceResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.HttpMethod;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.commons.lang3.StringUtils;

public class RestCall extends ServiceCall {

  public RestCall(String rootPath, int timeout, ObjectMapper objectMapper) {
    super(rootPath, timeout, objectMapper);
  }

  @Override
  public ServiceResponse callWithStrBody
  (String httpMethod, String url, String params) throws Exception {
    URL destUrl = new URL(url);
    HttpURLConnection connection = (HttpURLConnection)destUrl.openConnection();
    connection.setRequestMethod(httpMethod);
    RequestUtil.setRestRequestProperties(connection);
    connection.setConnectTimeout(timeout);
    connection.setReadTimeout(timeout);
    connection.setDoOutput(
      StringUtils.isNotEmpty(params)
      && !httpMethod.equalsIgnoreCase(HttpMethod.GET)
    );
    byte[] postOutput = null;
    if(connection.getDoOutput()) {
      postOutput = params.getBytes();
      connection.setFixedLengthStreamingMode(postOutput.length);
    }
    connection.connect();
    if(connection.getDoOutput()) {
      try(OutputStream postOs = connection.getOutputStream()) {
        postOs.write(postOutput);
      }
    }
    int code = connection.getResponseCode();
    String body = "";
    try(
      InputStream inputStream = code == HttpURLConnection.HTTP_OK
      ? connection.getInputStream() : connection.getErrorStream()
    ) {
      if(inputStream != null) {
        try(BufferedReader reader
        = new BufferedReader(new InputStreamReader(inputStream))) {
          String inputLine;
          while((inputLine = reader.readLine()) != null) {
            body += inputLine;
          }
        }
      }
    }
    return new ServiceResponse(code, body);
  }

}
