package biz.softfor.vaadin;

import biz.softfor.jpa.crud.querygraph.ColumnDescr;
import biz.softfor.jpa.crud.querygraph.EntityInf;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.util.api.Identifiable;
import biz.softfor.vaadin.field.grid.GridField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EntityFormColumns
<K extends Number, E extends Identifiable<K>, WOR extends Identifiable<K>>
extends LinkedHashMap<String, Component> implements Secured {

  public final EntityInf<K, E, WOR> entityInf;
  public final boolean addEnabled;
  private final boolean accessible;
  private final boolean readOnly;

  public EntityFormColumns(
    Class<E> clazz
  , LinkedHashMap<String, Component> columns
  , SecurityMgr securityMgr
  ) {
    entityInf = ColumnDescr.getInf(clazz);
    boolean ae = true;
    boolean ro = true;
    for(Map.Entry<String, Component> me : columns.entrySet()) {
      String fieldName = me.getKey();
      Component c = me.getValue();
      List<String> involvedFields;
      if(c instanceof DbNamedColumn cDbNamedColumn) {
        involvedFields = cDbNamedColumn.involvedFields();
      } else {
        involvedFields = Collections.EMPTY_LIST;
      }
      RequiredSecured rs = new RequiredSecured
      (securityMgr, entityInf.cds, fieldName, involvedFields);
      boolean isGrid = c instanceof GridField;
      if(rs.isAccessible() && (!isGrid || ((GridField)c).columns.isAccessible())) {
        put(fieldName, c);
        if(!((HasValue)c).isReadOnly()) {
          if(rs.isReadOnly()) {
            ((HasValue)c).setReadOnly(true);
          } else {
            ro = false;
          }
        }
      } else if(rs.required()) {
        ae = false;
      }
    }
    addEnabled = ae;
    accessible = !isEmpty();
    readOnly = ro;
  }

  protected EntityFormColumns
  (Class<E> clazz, LinkedHashMap<String, Component> columns) {
    entityInf = ColumnDescr.getInf(clazz);
    putAll(columns);
    addEnabled = false;
    accessible = true;
    readOnly = false;
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
