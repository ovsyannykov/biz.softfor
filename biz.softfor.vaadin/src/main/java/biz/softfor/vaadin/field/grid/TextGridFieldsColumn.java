package biz.softfor.vaadin.field.grid;

import biz.softfor.vaadin.dbgrid.TextDbGridColumn;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.apache.commons.lang3.StringUtils;

public class TextGridFieldsColumn<E>
extends GridFieldColumn<E, String, TextField, String> {

  public static <E> BiFunction<String, E, Boolean> defaultFilter
  (Function<E, String> getter) {
    return (value, item) -> StringUtils.isBlank(value)
    || StringUtils.containsIgnoreCase(getter.apply(item), value);
  }

  public TextGridFieldsColumn
  (String dbName, Renderer<E> renderer, BiFunction<String, E, Boolean> filter) {
    super(dbName, new TextField(), renderer, filter);
    TextDbGridColumn.configure(component);
    component.setValueChangeMode(ValueChangeMode.EAGER);
  }

  public TextGridFieldsColumn(String dbName, Function<E, String> getter) {
    this(dbName, null, defaultFilter(getter));
  }

}
