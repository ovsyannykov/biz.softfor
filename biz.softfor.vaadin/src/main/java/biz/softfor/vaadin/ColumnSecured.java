package biz.softfor.vaadin;

import biz.softfor.jpa.crud.querygraph.ColumnDescr;
import biz.softfor.spring.security.SecurityUtil;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.util.StringUtil;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ColumnSecured implements Secured {

  private final boolean required;
  private final boolean accessible;
  private final boolean readOnly;

  public ColumnSecured(
    SecurityMgr securityMgr
  , Map<String, ColumnDescr> cds
  , String field
  , List<String> involvedFields
  ) {
    boolean req = false;
    boolean acc = true;
    boolean ro = false;
    Collection<String> groups = SecurityUtil.groups();
    String[] fieldParts = field.split(StringUtil.FIELDS_DELIMITER_REGEX);
    for(int i = 0; i < fieldParts.length; ++i) {
      String fieldPart = fieldParts[i];
      ColumnDescr cd = cds.get(fieldPart);
      if(cd == null) {
        acc = false;
        break;
      }
      if(!securityMgr.isAllowed(cd.roleId, groups)) {
        acc = false;
      }
      if(!ro && !securityMgr.isAllowed(cd.updateRoleId, groups)) {
        ro = true;
      }
      if(i < fieldParts.length - 1) {
        cds = ColumnDescr.get(cd.clazz);
      } else {
        req = cds.get(fieldPart).required;
      }
    }
    required = req;
    accessible = acc && isInvolvedFieldsAccessible
    (securityMgr, cds, fieldParts[fieldParts.length - 1], involvedFields);
    readOnly = ro;
  }

  @Override
  public boolean isAccessible() {
    return accessible;
  }

  @Override
  public boolean isReadOnly() {
    return readOnly;
  }

  public boolean required() {
    return required;
  }

  private static boolean isInvolvedFieldsAccessible(
    SecurityMgr securityMgr
  , Map<String, ColumnDescr> cds
  , String columnName
  , List<String> involvedFields
  ) {
    boolean result = true;
    Collection<String> groups = SecurityUtil.groups();
    Class<?> clazz = cds.get(columnName).clazz;
    RETURN:
    for(String field : involvedFields) {
      String[] fieldParts = field.split(StringUtil.FIELDS_DELIMITER_REGEX);
      for(int i = 0; i < fieldParts.length; ++i) {
        ColumnDescr cd = ColumnDescr.get(clazz).get(fieldParts[i]);
        if(cd == null || !securityMgr.isAllowed(cd.roleId, groups)) {
          result = false;
          break RETURN;
        }
        if(i < fieldParts.length - 1) {
          clazz = cd.clazz;
        }
      }
    }
    return result;
  }

}
