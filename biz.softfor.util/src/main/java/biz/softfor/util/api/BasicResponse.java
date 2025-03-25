package biz.softfor.util.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@JsonIgnoreProperties({ InternalResponse.IS_EMPTY_FOR_UPDATE })
public class BasicResponse<T> {

  //Server errors <0
  public final static int SERVER_ERROR = -1;

  public final static int OK = 0;
  public final static String OK_DESCR = "OK";

  //Client errors >0
  public final static int CLIENT = 1;
  public final static int ACCESS_DENIED = 2;
  public final static int REQUEST_PARSE = 3;
  public final static int BAD_REQUEST = 4;
  public final static int NOT_FOUND = 5;
  public final static int ROWS_ON_PAGE_EXCEED_LIMIT = 6;
  public final static int RESPONSE_PARSE = 1000;

  public final static String Access_denied = "Access_denied";
  public final static String Response_parse_error = "Response_parse_error";

  private int status;
  public final static String STATUS = "status";

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  private String descr;
  public final static String DESCR = "descr";

  public String getDescr() {
    return descr;
  }

  public void setDescr(String descr) {
    this.descr = descr;
  }

  private T data;
  public final static String DATA = "data";

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  private ErrorData errorData;

  public ErrorData getErrorData() {
    return errorData;
  }

  public void setErrorData(ErrorData errorData) {
    this.errorData = errorData;
  }

  private String correlationId;

  public String getCorrelationId() {
    return correlationId;
  }

  public void setCorrelationId(String correlationId) {
    this.correlationId = correlationId;
  }

  private Date timestamp;
  public final static String TIMESTAMP = "timestamp";

  public Date getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }

  public BasicResponse(int status, String descr, T data, ErrorData errorData) {
    this.status = status;
    this.descr = descr;
    this.data = data;
    this.errorData = errorData;
  }

  public BasicResponse(int status, String descr, T data) {
    this(status, descr, data, null);
  }

  public BasicResponse() {
    this(OK, OK_DESCR, null, null);
  }

  public BasicResponse(int status, String descr) {
    this(status, descr, null, null);
  }

  public BasicResponse(T data) {
    this(OK, OK_DESCR, data, null);
  }

  public BasicResponse(int status, String descr, ErrorData errorData) {
    this(status, descr, null, errorData);
  }

  public BasicResponse(ErrorData errorData) {
    this(errorData.status(), errorData.message(), null, errorData);
  }

  @JsonIgnore
  public boolean isOk() {
    return status == OK;
  }

  public void errorDescription
  (int status, String attribute, String message, Object... args) {
    this.status = status;
    if(OK_DESCR.equalsIgnoreCase(descr)) {
      descr = "";
    }
    Object[] a = new Object[args.length + 1];
    a[0] = attribute;
    for(int i = args.length; --i >= 0;) {
      a[i + 1] = args[i];
    }
    if(!descr.isEmpty()) {
      descr += "\n";
    }
    descr += String.format(message, a);
  }

}
