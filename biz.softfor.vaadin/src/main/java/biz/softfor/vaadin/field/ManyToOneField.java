package biz.softfor.vaadin.field;

import biz.softfor.util.api.Identifiable;
import biz.softfor.vaadin.dbgrid.DbGrid;
import java.util.List;
import java.util.function.Function;

public class ManyToOneField<K extends Number, E extends Identifiable<K>>
extends ManyToOneBasicField<K, E, E> {

  public ManyToOneField(
    String name
  , DbGrid<K, E, ? extends Identifiable<K>> dbGrid
  , Function<E, String> label
  , Function<E, String> detail
  , List<String> involvedFields
  ) {
    super(name, dbGrid, label, detail, involvedFields);
  }

  @Override
  public E getModelValue() {
    return dbGrid.grid.asSingleSelect().getValue();
  }

  @Override
  protected void setPresentationValue(E v) {
    value = v;
    viewCtl.setValue(v == null ? "" : label.apply(v));
    viewCtl.setTooltipText(v == null ? "" : detail.apply(v));
  }

}
