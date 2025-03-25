package biz.softfor.spring;

public class Constants {

  public final static String SERVER_PORT_VALUE
  = "${server.port:" + biz.softfor.util.Constants.SERVER_PORT_DEFAULT + "}";

}
