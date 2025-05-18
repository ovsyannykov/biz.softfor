package biz.softfor.vaadin.dbgrid;

import biz.softfor.util.api.Identifiable;
import biz.softfor.util.api.filter.FilterId;
import biz.softfor.vaadin.field.ManyToOneBasicField;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class ManyToOneGridColumnComponent
<K extends Number, E extends Identifiable<K>>
extends ManyToOneBasicField<K, E, Set<E>> implements LocaleChangeObserver {

  public final static String Selected_only = "Selected_only";

  private final Checkbox selectedOnly;
  private Consumer<FilterId<K>> originalFilter;

  public ManyToOneGridColumnComponent(
    String name
  , DbGrid<K, E, ? extends Identifiable<K>> dbGrid
  , Function<E, String> label
  , Function<E, String> detail
  , List<String> involvedFields
  ) {
    super(name, dbGrid, label, detail, involvedFields);
    this.dbGrid.grid.setSelectionMode(Grid.SelectionMode.MULTI);
    selectedOnly = new Checkbox(getTranslation(Selected_only), e -> {
      Consumer<FilterId<K>> selectedOnlyFilter;
      if(e.getValue()) {
        originalFilter = this.dbGrid.filter();
        selectedOnlyFilter = f -> {
          f.setId(Identifiable.ids(getModelValue()));
          if(originalFilter != null) {
            originalFilter.accept(f);
          }
        };
      } else {
        selectedOnlyFilter = originalFilter;
      }
      this.dbGrid.filter(selectedOnlyFilter);
    });
    this.dbGrid.toolbar.add(selectedOnly);
    this.dbGrid.clearReg.remove();
    this.dbGrid.clearBtn.addClickListener(e -> {
      this.dbGrid.filterClear();
      selectedOnly.setValue(Boolean.FALSE);
    });
    viewCtl.addThemeVariants(TextFieldVariant.LUMO_SMALL);
    viewCtl.setWidthFull();
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

  @Override
  public void localeChange(LocaleChangeEvent event) {
    selectedOnly.setLabel(getTranslation(Selected_only));
  }

}
