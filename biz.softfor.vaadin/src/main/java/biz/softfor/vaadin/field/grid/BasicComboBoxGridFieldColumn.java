package biz.softfor.vaadin.field.grid;

import biz.softfor.util.Labeled;
import biz.softfor.vaadin.dbgrid.BasicComboBoxDbGridColumn;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.apache.commons.collections4.CollectionUtils;

public class BasicComboBoxGridFieldColumn<E, V, VL extends Labeled>
extends GridFieldColumn<E, V, MultiSelectComboBox<VL>, Set<VL>> {

  public static <M, E> BiFunction<Set<E>, M, Boolean> defaultFilter
  (Function<M, E> getter) {
    return (cv, m) -> CollectionUtils.isEmpty(cv) || cv.contains(getter.apply(m));
  }

  public BasicComboBoxGridFieldColumn(
    String dbName
  , VL[] items
  , Renderer<E> renderer
  , BiFunction<Set<VL>, E, Boolean> filter
  ) {
    super(dbName, new MultiSelectComboBox<>(), renderer, filter);
    BasicComboBoxDbGridColumn.configure(component, items);
  }

  @Override
  public void localeChangeColumn(LocaleChangeEvent event) {
    super.localeChangeColumn(event);
    BasicComboBoxDbGridColumn.localeChange(component);
  }

}
