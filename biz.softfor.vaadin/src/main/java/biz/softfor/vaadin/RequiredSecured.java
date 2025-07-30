package biz.softfor.vaadin;

import biz.softfor.jpa.crud.querygraph.ColumnDescr;
import biz.softfor.spring.security.SecurityUtil;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.util.StringUtil;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class RequiredSecured implements Secured {

  private final boolean required;
  private final boolean accessible;
  private final boolean readOnly;

  public RequiredSecured(
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
    if(acc) {
      boolean isInvolvedFieldsAccessible = true;
      Class<?> clazz = cds.get(fieldParts[fieldParts.length - 1]).clazz;
      RETURN:
      for(String invf : involvedFields) {
        String[] invfp = invf.split(StringUtil.FIELDS_DELIMITER_REGEX);
        for(int i = 0; i < invfp.length; ++i) {
          ColumnDescr cd = ColumnDescr.get(clazz).get(invfp[i]);
          if(cd == null || !securityMgr.isAllowed(cd.roleId, groups)) {
            isInvolvedFieldsAccessible = false;
            break RETURN;
          }
          if(i < invfp.length - 1) {
            clazz = cd.clazz;
          }
        }
      }
      if(!isInvolvedFieldsAccessible) {
        acc = false;
      }
    }
    accessible = acc;
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

}
