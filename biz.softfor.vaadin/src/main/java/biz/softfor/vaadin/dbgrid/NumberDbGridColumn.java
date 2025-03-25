package biz.softfor.vaadin.dbgrid;

import biz.softfor.util.Reflection;
import biz.softfor.util.api.filter.FilterId;
import biz.softfor.vaadin.CSS;
import com.vaadin.flow.component.shared.HasThemeVariant;
import com.vaadin.flow.component.textfield.AbstractNumberField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import java.util.function.BiConsumer;

public class NumberDbGridColumn
<E, N extends Number, NF extends AbstractNumberField<NF, N>, F extends FilterId>
extends DbGridColumn<E, N, NF, N, F> {

  public static <N extends Number, F extends AbstractNumberField<F, N>> void
  configure(F component) {
    component.setClearButtonVisible(true);
    ((HasThemeVariant<TextFieldVariant>)component).addThemeVariants
    (TextFieldVariant.LUMO_ALIGN_RIGHT, TextFieldVariant.LUMO_SMALL);
    component.setWidthFull();
    component.setMaxWidth(CSS.PC100);
  }

  public static
  <N extends Number, NF extends AbstractNumberField<NF, N>, F extends FilterId>
  BiConsumer<F, NF> defaultFilter(BiConsumer<F, N> setter) {
    return (requestFilter, component) -> {
      N v = component.getValue();
      if(v != null) {
        setter.accept(requestFilter, v);
      }
    };
  }

  public NumberDbGridColumn
  (String dbName, Class<NF> clazz, Renderer<E> renderer, BiConsumer<F, NF> filter) {
    super(dbName, Reflection.newInstance(clazz), renderer, filter);
    configure(component);
    component.setValueChangeMode(ValueChangeMode.LAZY);
  }

  public NumberDbGridColumn
  (String dbName, Class<NF> clazz, BiConsumer<F, N> setter) {
    this(dbName, clazz, null, defaultFilter(setter));
  }

}
