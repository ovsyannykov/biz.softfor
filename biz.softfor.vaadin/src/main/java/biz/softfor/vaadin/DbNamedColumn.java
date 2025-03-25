package biz.softfor.vaadin;

import biz.softfor.util.StringUtil;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public interface DbNamedColumn {

  public String dbName();

  public default List<String> involvedFields() {
    return Collections.EMPTY_LIST;
  }

  public static List<String> fields(
    List<String> fields
  , Collection<? extends DbNamedColumn> columns
  , String prefix
  ) {
    for(DbNamedColumn c : columns) {
      String f = c.dbName();
      if(StringUtils.isNotBlank(prefix)) {
        f = StringUtil.field(prefix, f);
      }
      List<String> involvedFields = c.involvedFields();
      if(involvedFields.isEmpty()) {
        fields.add(f);
      } else {
        for(String rf : involvedFields) {
          fields.add(StringUtil.field(f, rf));
        }
      }
    }
    return fields;
  }

}
