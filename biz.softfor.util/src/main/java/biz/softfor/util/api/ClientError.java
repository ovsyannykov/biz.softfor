package biz.softfor.util.api;

import biz.softfor.util.AbstractError;

public class ClientError extends AbstractError {

  private final static long serialVersionUID = 0L;

  public ClientError(String message, Throwable cause, int code) {
    super(message, cause, code);
  }

  public ClientError(Throwable cause, int code) {
    super(cause, code);
  }

  public ClientError(String message, Throwable cause) {
    super(message, cause, BasicResponse.CLIENT);
  }

  public ClientError(String message) {
    super(message, BasicResponse.CLIENT);
  }

  public ClientError(Throwable cause) {
    super(cause, BasicResponse.CLIENT);
  }

}
