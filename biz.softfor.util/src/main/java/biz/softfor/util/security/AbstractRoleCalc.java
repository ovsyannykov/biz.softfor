package biz.softfor.util.security;

import biz.softfor.util.StringUtil;

public abstract class AbstractRoleCalc {

  public final static int CLASS = 0;
  public final static int FIELD = 1;
  public final static int METHOD = 2;

  public final boolean updateFor;
  protected final String nameFormat;
  private final int typ;

  public abstract DefaultAccess defaultAccess();
  public abstract boolean deniedForAll();
  public abstract String description();
  public abstract boolean ignore();
  public abstract boolean isUrl();
  public abstract String name();
  public abstract String objName();

  protected AbstractRoleCalc(boolean updateFor, int typ, String nameFormat) {
    this.updateFor = updateFor;
    this.typ = typ;
    this.nameFormat = nameFormat;
  }

  public final long id() {
    return id(updateFor, objName(), typ);
  }

  @Override
  public String toString() {
    return id() + " (" + (updateFor ? "~" + objName() : objName()) + ")";
  }

  public static long id(boolean updateFor, String objName, int typ) {
    long result = StringUtil.longHash(objName) * 31 + typ;
    if(updateFor) {
      result = result * 31 + 1;
    }
    return result;
  }

}
