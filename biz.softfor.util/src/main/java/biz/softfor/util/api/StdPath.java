package biz.softfor.util.api;

public class StdPath {

  public final static String ROOT = "/";

  public final static String ERROR = ROOT + "error";
  public final static String PING = ROOT + "ping";
  public final static String PINGDB = ROOT + "pingdb";

  public final static String CREATE = "create";
  public final static String READ = "read";
  public final static String UPDATE = "update";
  public final static String DELETE = "delete";

  public final static String LOGIN = ROOT + "login";
  public final static String LOGIN_METHOD = "POST";
  public final static String LOGOUT = ROOT + "logout";
  public final static String LOGOUT_METHOD = "POST";
  public final static String REFRESH_TOKEN = ROOT + "refreshtoken";
  public final static String REFRESH_TOKEN_METHOD = "POST";
  public final static String REGISTRATION = ROOT + "registration";
  public final static String REGISTRATION_METHOD = "POST";

  public final static String LOCALHOST = "localhost";
  public final static String LOCATION_HOST = "http://127.0.0.1";

  public static String locationUri(int port) {
    return LOCATION_HOST + ":" + port;
  }

}
