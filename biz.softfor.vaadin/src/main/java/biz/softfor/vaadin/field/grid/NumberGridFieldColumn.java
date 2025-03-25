package biz.softfor.vaadin.field.grid;

import biz.softfor.util.Reflection;
import biz.softfor.vaadin.dbgrid.NumberDbGridColumn;
import com.vaadin.flow.component.textfield.AbstractNumberField;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import java.util.function.BiFunction;
import java.util.function.Function;

public class NumberGridFieldColumn
<E, N extends Number, NF extends AbstractNumberField<NF, N>>
extends GridFieldColumn<E, N, NF, N> {

  public static <E, N extends Number> BiFunction<N, E, Boolean> defaultFilter
  (Function<E, N> getter) {
    return (value, item) -> value == null || getter.apply(item).equals(value);
  }

  public NumberGridFieldColumn(
    String dbName
  , Class<NF> clazz
  , Renderer<E> renderer
  , BiFunction<N, E, Boolean> filter
  ) {
    super(dbName, Reflection.newInstance(clazz), renderer, filter);
    NumberDbGridColumn.configure(component);
    component.setValueChangeMode(ValueChangeMode.EAGER);
  }

  public NumberGridFieldColumn
  (String dbName, Class<NF> clazz, Function<E, N> getter) {
    this(dbName, clazz, null, defaultFilter(getter));
  }

}
