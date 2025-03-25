package biz.softfor.vaadin.dbgrid;

import biz.softfor.util.api.Identifiable;
import biz.softfor.vaadin.CSS;
import biz.softfor.vaadin.field.ManyToOneBasicField;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class ManyToOneGridColumnComponent
<K extends Number, E extends Identifiable<K>>
extends ManyToOneBasicField<K, E, Set<E>> {

  public ManyToOneGridColumnComponent(
    String name
  , DbGrid<K, E, ? extends Identifiable<K>> dbGrid
  , Function<E, String> label
  , Function<E, String> detail
  , List<String> involvedFields
  ) {
    super(name, dbGrid, label, detail, involvedFields);
  }

  public ManyToOneGridColumnComponent configure() {
    dbGrid.grid.setSelectionMode(Grid.SelectionMode.MULTI);
    setClearButtonVisible(true);
    viewCtl.addThemeVariants(TextFieldVariant.LUMO_SMALL);
    viewCtl.setWidthFull();
    viewCtl.setMaxWidth(CSS.PC100);
    return this;
  }

  @Override
  public Set<E> getModelValue() {
    return dbGrid.grid.asMultiSelect().getValue();
  }

  @Override
  protected void setPresentationValue(Set<E> values) {
    if(values == null) {
      values = Set.of();
    }
    value = values;
    String l = "";
    String t = "";
    for(E v : values) {
      if(!l.isEmpty()) {
        l += ", ";
      }
      if(!t.isEmpty()) {
        t += ", ";
      }
      l += label.apply(v);
      t += detail.apply(v);
    }
    viewCtl.setValue(l);
    viewCtl.setTooltipText(t);
  }

}
