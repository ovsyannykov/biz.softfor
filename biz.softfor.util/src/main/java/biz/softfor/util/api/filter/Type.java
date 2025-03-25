package biz.softfor.util.api.filter;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.sql.Types;

public enum Type {

    VARCHAR("varchar", Types.VARCHAR, 0)
  , INTEGER("integer", Types.INTEGER, 0)
  , DATETIME("datetime", Types.TIMESTAMP, 26)
  , TIMESTAMP("timestamp", Types.TIMESTAMP, 26)
  , TEXT("text", Types.VARCHAR, 0)
  , BIT("bit", Types.BIT, 1)
  , CHAR("char", Types.CHAR, 3)
  ;

  private final String type;
  private final int jdbcType;
  private final int sqlSize;

  private final static Type[] VALUES = Type.values();

  private Type(String type, int jdbcType, int sqlSize) {
    this.type = type;
    this.jdbcType = jdbcType;
    this.sqlSize = sqlSize;
  }

  public int getJdbcType() {
    return jdbcType;
  }

  public int getSqlSize() {
    return sqlSize;
  }

  @JsonCreator
  public static Type of(String v) {
    Type result = VARCHAR;
    v = v.toLowerCase();
    for(Type t : VALUES) {
      if(v.equals(t.type)) {
        result = t;
        break;
      }
    }
    return result;
  }

}
