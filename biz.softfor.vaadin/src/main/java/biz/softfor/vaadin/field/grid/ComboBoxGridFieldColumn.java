package biz.softfor.vaadin.field.grid;

import biz.softfor.util.Labeled;
import biz.softfor.vaadin.dbgrid.BasicComboBoxDbGridColumn;
import com.vaadin.flow.data.renderer.Renderer;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ComboBoxGridFieldColumn<E, V extends Labeled>
extends BasicComboBoxGridFieldColumn<E, V, V> {

  public ComboBoxGridFieldColumn(
    String dbName
  , V[] items
  , Renderer<E> renderer
  , BiFunction<Set<V>, E, Boolean> filter
  ) {
    super(dbName, items, renderer, filter);
  }

  public ComboBoxGridFieldColumn(String dbName, V[] items, Function<E, V> getter) {
    this(dbName
    , items
    , BasicComboBoxDbGridColumn.defaultRenderer(getter)
    , defaultFilter(getter)
    );
  }

}
