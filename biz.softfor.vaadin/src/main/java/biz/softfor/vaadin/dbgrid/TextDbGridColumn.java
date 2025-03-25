package biz.softfor.vaadin.dbgrid;

import biz.softfor.util.api.filter.FilterId;
import biz.softfor.vaadin.CSS;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import java.util.function.BiConsumer;
import org.apache.commons.lang3.StringUtils;

public class TextDbGridColumn<E, F extends FilterId>
extends DbGridColumn<E, String, TextField, String, F> {

  public static void configure(TextField component) {
    component.setClearButtonVisible(true);
    component.addThemeVariants(TextFieldVariant.LUMO_SMALL);
    component.setWidthFull();
    component.setMaxWidth(CSS.PC100);
  }

  public static <F extends FilterId> BiConsumer<F, TextField> defaultFilter
  (BiConsumer<F, String> setter) {
    return (requestFilter, component) -> {
      String v = component.getValue();
      if(StringUtils.isNotBlank(v)) {
        setter.accept(requestFilter, "%" + v + "%");
      }
    };
  }

  public TextDbGridColumn
  (String dbName, Renderer<E> renderer, BiConsumer<F, TextField> filter) {
    super(dbName, new TextField(), renderer, filter);
    configure(component);
    component.setValueChangeMode(ValueChangeMode.LAZY);
  }

  public TextDbGridColumn(String dbName, BiConsumer<F, String> setter) {
    this(dbName, null, defaultFilter(setter));
  }

}
