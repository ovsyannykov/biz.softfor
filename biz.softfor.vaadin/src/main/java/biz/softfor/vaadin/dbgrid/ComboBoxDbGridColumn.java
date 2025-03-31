package biz.softfor.vaadin.dbgrid;

import biz.softfor.util.Labeled;
import biz.softfor.util.api.filter.FilterId;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.data.renderer.Renderer;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import org.apache.commons.collections4.CollectionUtils;

public class ComboBoxDbGridColumn<E, V extends Labeled, F extends FilterId>
extends BasicComboBoxDbGridColumn<E, V, V, F> {

  public static <V extends Labeled, F extends FilterId>
  BiConsumer<F, MultiSelectComboBox<V>> defaultFilter
  (BiConsumer<F, Set<V>> setter) {
    return (requestFilter, component) -> {
      Set<V> v = component.getValue();
      if(CollectionUtils.isNotEmpty(v)) {
        setter.accept(requestFilter, v);
      }
    };
  }

  public ComboBoxDbGridColumn(
    String dbName
  , V[] items
  , Renderer<E> renderer
  , BiConsumer<F, MultiSelectComboBox<V>> filter
  ) {
    super(dbName, items, renderer, filter);
  }

  public ComboBoxDbGridColumn
  (String dbName, V[] items, Function<E, V> getter, BiConsumer<F, Set<V>> setter) {
    this(dbName, items, defaultRenderer(getter), defaultFilter(setter));
  }

}
