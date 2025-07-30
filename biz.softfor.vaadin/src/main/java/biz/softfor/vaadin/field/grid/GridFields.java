package biz.softfor.vaadin.field.grid;

import biz.softfor.jpa.crud.querygraph.ColumnDescr;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.util.api.Identifiable;
import biz.softfor.vaadin.RequiredSecured;
import biz.softfor.vaadin.Secured;
import java.util.ArrayList;
import java.util.Map;

public class GridFields<K extends Number, E extends Identifiable<K>>
extends ArrayList<GridField<?, ?, E, ?>> implements Secured {

  public final static GridFields EMPTY = new GridFields();

  private final boolean accessible;
  private final boolean readOnly;

  public GridFields
  (SecurityMgr securityMgr, Class<E> clazz, GridField<?, ?, E, ?>... columns) {
    boolean ro = true;
    if(columns != null) {
      Map<String, ColumnDescr> cds = ColumnDescr.get(clazz);
      for(GridField<?, ?, E, ?> c : columns) {
        if(c.columns.isAccessible()) {
          RequiredSecured rs
          = new RequiredSecured(securityMgr, cds, c.dbName(), c.involvedFields());
          if(rs.isAccessible()) {
            add(c);
            if(!rs.isReadOnly()) {
              ro = false;
            }
          }
        }
      }
    }
    accessible = !isEmpty();
    readOnly = ro;
  }

  public GridFields() {
    accessible = false;
    readOnly = true;
  }

  @Override
  public boolean isAccessible() {
    return accessible;
  }

  @Override
  public boolean isReadOnly() {
    return readOnly;
  }

}
