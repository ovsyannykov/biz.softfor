package biz.softfor.vaadin.dbgrid;

import biz.softfor.jpa.crud.querygraph.ColumnDescr;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.util.StringUtil;
import biz.softfor.util.api.Identifiable;
import biz.softfor.util.api.Order;
import biz.softfor.util.api.filter.FilterId;
import biz.softfor.vaadin.ColumnSecured;
import biz.softfor.vaadin.DefaultSort;
import biz.softfor.vaadin.Secured;
import com.vaadin.flow.component.AbstractField;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DbGridColumns<K extends Number, E extends Identifiable<K>>
extends ArrayList
<DbGridColumn<E, ?, ? extends AbstractField, ?, ? extends FilterId<K>>>
implements DefaultSort, Secured {

  public final static DbGridColumns EMPTY = new DbGridColumns();

  public final String title;
  private final boolean accessible;
  private final boolean readOnly;

  public DbGridColumns(
    String title
  , SecurityMgr securityMgr
  , Class<E> clazz
  , DbGridColumn<E, ?, ? extends AbstractField, ?, ? extends FilterId<K>>...
    columns
  ) {
    this.title = title;
    boolean acc = false;
    boolean ro = true;
    if(columns != null) {
      Map<String, ColumnDescr> cds = ColumnDescr.get(clazz);
      for(DbGridColumn<E, ?, ? extends AbstractField, ?, ? extends FilterId<K>>
      c : columns) {
        String columnName = c.dbName();
        ColumnSecured cs
        = new ColumnSecured(securityMgr, cds, c.dbName(), c.involvedFields());
        if(cs.isAccessible()) {
          add(c);
          if(!acc && !columnName.equals(Identifiable.ID)
          && !columnName.endsWith(StringUtil.FIELDS_DELIMITER + Identifiable.ID)) {
            acc = true;
          }
          if(!cs.isReadOnly()) {
            ro = false;
          }
        }
      }
    }
    accessible = acc;
    readOnly = ro;
  }

  public DbGridColumns() {
    title = "";
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

  @Override
  public List<Order> sort() {
    return List.of();
  }

}
