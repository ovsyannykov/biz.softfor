package biz.softfor.vaadin.field.grid;

import biz.softfor.util.BooleansEnum;
import biz.softfor.vaadin.dbgrid.BasicComboBoxDbGridColumn;
import com.vaadin.flow.data.renderer.Renderer;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class BoolGridFieldColumn<E>
extends BasicComboBoxGridFieldColumn<E, Boolean, BooleansEnum> {

  public BoolGridFieldColumn(
    String dbName
  , BooleansEnum[] items
  , Renderer<E> renderer
  , BiFunction<Set<BooleansEnum>, E, Boolean> filter
  ) {
    super(dbName, items, renderer, filter);
  }

  public BoolGridFieldColumn
  (String dbName, BooleansEnum[] items, Function<E, Boolean> getter) {
    this(dbName
    , items
    , BasicComboBoxDbGridColumn.defaultRenderer(e -> BooleansEnum.of(getter.apply(e)))
    , defaultFilter(e -> BooleansEnum.of(getter.apply(e)))
    );
  }

}
