package biz.softfor.vaadin.field.grid;

import biz.softfor.jpa.crud.querygraph.ColumnDescr;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.util.StringUtil;
import biz.softfor.util.api.Identifiable;
import biz.softfor.vaadin.RequiredSecured;
import biz.softfor.vaadin.DefaultSort;
import biz.softfor.vaadin.Secured;
import com.vaadin.flow.component.AbstractField;
import java.util.ArrayList;
import java.util.Map;

public abstract class GridFieldColumns<K extends Number, E extends Identifiable<K>>
extends ArrayList<GridFieldColumn<E, ?, ? extends AbstractField, ?>>
implements DefaultSort, Secured {

  private final boolean accessible;

  public GridFieldColumns(
    SecurityMgr securityMgr
  , Class<E> clazz
  , GridFieldColumn<E, ?, ? extends AbstractField, ?>... columns
  ) {
    boolean acc = false;
    if(columns != null) {
      Map<String, ColumnDescr> cds = ColumnDescr.get(clazz);
      for(GridFieldColumn<E, ?, ? extends AbstractField, ?> c : columns) {
        String columnName = c.dbName();
        RequiredSecured rs
        = new RequiredSecured(securityMgr, cds, columnName, c.involvedFields());
        if(rs.isAccessible()) {
          add(c);
          if(!acc && !columnName.equals(Identifiable.ID)
          && !columnName.endsWith(StringUtil.FIELDS_DELIMITER + Identifiable.ID)) {
            acc = true;
          }
        }
      }
    }
    accessible = acc;
  }

  @Override
  public boolean isAccessible() {
    return accessible;
  }

  @Override
  public boolean isReadOnly() {
    return true;
  }

}
