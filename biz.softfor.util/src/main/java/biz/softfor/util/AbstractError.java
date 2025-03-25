package biz.softfor.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class AbstractError extends RuntimeException {

  public final int code;

  public AbstractError(String message, Throwable cause, int code) {
    super(message, cause);
    this.code = code;
  }

  public AbstractError(Throwable cause, int code) {
    super(cause);
    this.code = code;
  }

  public AbstractError(String message, int code) {
    super(message);
    this.code = code;
  }

  public static String stackTraceToString(Exception ex) {
    String result = "";
    if(ex != null) {
      StringWriter sw = new StringWriter();
      ex.printStackTrace(new PrintWriter(sw));
      result = sw.toString();
    }
    return result;
  }
}
