package biz.softfor.vaadin.dbgrid;

import biz.softfor.util.api.Identifiable;
import biz.softfor.util.api.ReadRequest;
import biz.softfor.util.api.filter.FilterId;
import biz.softfor.vaadin.field.ManyToOneBasicField;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBoxVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class ManyToOneGridColumnComponent
<K extends Number, E extends Identifiable<K>, F extends FilterId<K>>
extends ManyToOneBasicField<K, E, F, Set<E>, MultiSelectComboBox<E>>
implements LocaleChangeObserver {

  public final static String Selected_only = "Selected_only";

  private final Checkbox selectedOnly;
  private Consumer<FilterId<K>> originalFilter;

  public ManyToOneGridColumnComponent(
    String name
  , DbGrid<K, E, ? extends Identifiable<K>, F> dbGrid
  , ItemLabelGenerator<E> label
  , Function<E, String> detail
  , List<String> involvedFields
  , BiConsumer<ReadRequest<K, F>, String> fillRequest
  ) {
    super(
      name
    , dbGrid
    , label
    , detail
    , involvedFields
    , fillRequest
    , new MultiSelectComboBox<>()
    , dbg -> dbg.grid.asMultiSelect().getValue()
    , (dbg, v) -> dbg.grid.asMultiSelect().setValue(v)
    , v -> {
        String result = "";
        for(E item : v) {
          if(!result.isEmpty()) {
            result += ", ";
          }
          result += detail.apply(item);
        }
        return result;
      }
    );
    this.dbGrid.grid.setSelectionMode(Grid.SelectionMode.MULTI);
    ((MultiSelectComboBox)viewCtl)
    .addThemeVariants(MultiSelectComboBoxVariant.LUMO_SMALL);
    ((MultiSelectComboBox)viewCtl).setSelectedItemsOnTop(true);
    selectedOnly = new Checkbox(getTranslation(Selected_only), e -> {
      Consumer<FilterId<K>> selectedOnlyFilter;
      if(e.getValue()) {
        originalFilter = this.dbGrid.filter();
        selectedOnlyFilter = f -> {
          f.setId(Identifiable.ids(generateModelValue()));
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
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    selectedOnly.setLabel(getTranslation(Selected_only));
  }

}
