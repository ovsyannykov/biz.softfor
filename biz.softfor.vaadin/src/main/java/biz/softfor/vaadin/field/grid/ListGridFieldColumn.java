package biz.softfor.vaadin.field.grid;

import biz.softfor.util.Labeled;
import biz.softfor.vaadin.dbgrid.ComboBoxDbGridColumn;
import com.vaadin.flow.data.renderer.Renderer;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ListGridFieldColumn<E, V extends Labeled>
extends ComboBoxGridFieldColumn<E, V, V> {

  public ListGridFieldColumn(
    String dbName
  , V[] items
  , Renderer<E> renderer
  , BiFunction<Set<V>, E, Boolean> filter
  ) {
    super(dbName, items, renderer, filter);
  }

  public ListGridFieldColumn(String dbName, V[] items, Function<E, V> getter) {
    this(
      dbName
    , items
    , ComboBoxDbGridColumn.defaultRenderer(getter)
    , defaultFilter(getter)
    );
  }

}
