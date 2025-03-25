package biz.softfor.util.api;

import biz.softfor.util.AbstractError;

public class ServerError extends AbstractError {

  public final static String SERVER_ERROR_MESSAGE = "Internal server error.";

  private final static long serialVersionUID = 0L;

  public ServerError(String message, Throwable cause) {
    super(message, cause, BasicResponse.SERVER_ERROR);
  }

  public ServerError(Throwable cause, int code) {
    super(SERVER_ERROR_MESSAGE, cause, code);
  }

  public ServerError(Throwable cause) {
    super(SERVER_ERROR_MESSAGE, cause, BasicResponse.SERVER_ERROR);
  }

  public ServerError(String message) {
    super(message, BasicResponse.SERVER_ERROR);
  }

}
