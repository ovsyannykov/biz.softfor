package biz.softfor.util;

import java.util.List;

public interface Constants {

  public final static String DESCR = "descr";
  public final static String DETAILS = "details";
  public final static String FULLNAME = "fullname";
  public final static String ID = "id";
  public final static String LANGUAGE = "language";
  public final static String NAME = "name";
  public final static String NOTE = "note";
  public final static String TYP = "typ";
  public final static String Unsupported_operation = "Unsupported_operation";

  public final static String MSSQL_DATASOURCE_PLATFORM = "mssql";
  public final static String MSSQL_SYSTEM_DATASOURCE_NAME = "master";

  public final static String MYSQL_DATASOURCE_PLATFORM = "mysql";
  public final static String MYSQL_SYSTEM_DATASOURCE_NAME = "mysql";

  public final static String REFLECTIONS_EXT = ".reflections";

  public final static String ROLE_PREFIX = "ROLE_";
  public final static String ROLE_ANONYMOUS = ROLE_PREFIX + "ANONYMOUS";
  public final static List<String> ANONYMOUS_ROLES = List.of(ROLE_ANONYMOUS);

  public final static int SERVER_PORT_DEFAULT = 8080;
  public final static String WS_ROOT_DEFAULT = "/ws";

}
