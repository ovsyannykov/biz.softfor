package biz.softfor.vaadin;

import biz.softfor.jpa.crud.querygraph.ColumnDescr;
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

  public final Class<E> clazz;
  public final Class<WOR> classWor;
  public final boolean addEnabled;
  private final boolean accessible;
  private final boolean readOnly;

  public EntityFormColumns(
    SecurityMgr securityMgr
  , Class<E> clazz
  , Class<WOR> classWor
  , LinkedHashMap<String, Component> columns
  ) {
    this.clazz = clazz;
    this.classWor = classWor;
    boolean ae = true;
    boolean ro = true;
    Map<String, ColumnDescr> cds = ColumnDescr.get(this.clazz);
    for(Map.Entry<String, Component> me : columns.entrySet()) {
      String fieldName = me.getKey();
      Component c = me.getValue();
      ColumnSecured cs;
      if(c instanceof DbNamedColumn cDbNamedColumn) {
        List<String> involvedFields = cDbNamedColumn.involvedFields();
        cs = new ColumnSecured(securityMgr, cds, fieldName, involvedFields);
      } else {
        cs = new ColumnSecured(securityMgr, cds, fieldName, Collections.EMPTY_LIST);
      }
      boolean isGrid = c instanceof GridField;
      if(cs.isAccessible() && (!isGrid || ((GridField)c).columns.isAccessible())) {
        put(fieldName, c);
        if(!((HasValue)c).isReadOnly()) {
          if(cs.isReadOnly()) {
            ((HasValue)c).setReadOnly(true);
          } else {
            ro = false;
          }
        }
      } else if(cs.required()) {
        ae = false;
      }
    }
    addEnabled = ae;
    accessible = !isEmpty();
    readOnly = ro;
  }

  protected EntityFormColumns(
    LinkedHashMap<String, Component> columns
  , Class<E> clazz
  , Class<WOR> classWor
  ) {
    putAll(columns);
    this.clazz = clazz;
    this.classWor = classWor;
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
