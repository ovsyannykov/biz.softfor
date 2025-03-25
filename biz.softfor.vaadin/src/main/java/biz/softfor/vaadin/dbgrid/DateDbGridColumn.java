package biz.softfor.vaadin.dbgrid;

import biz.softfor.util.Range;
import biz.softfor.util.api.filter.FilterId;
import biz.softfor.vaadin.CSS;
import biz.softfor.vaadin.field.DateRangePicker;
import com.vaadin.flow.data.renderer.Renderer;
import java.time.LocalDate;
import java.util.function.BiConsumer;

public class DateDbGridColumn<E, F extends FilterId>
extends DbGridColumn<E, Range<LocalDate>, DateRangePicker, Range<LocalDate>, F> {

  public static void configure(DateRangePicker component) {
    component.setWidthFull();
    component.setMaxWidth(CSS.PC100);
  }

  public static <F extends FilterId> BiConsumer<F, DateRangePicker> defaultFilter
  (BiConsumer<F, Range<LocalDate>> setter) {
    return (requestFilter, component) -> {
      Range<LocalDate> v = component.getValue();
      if(v != null) {
        setter.accept(requestFilter, v);
      }
    };
  }

  public DateDbGridColumn
  (String dbName, Renderer<E> renderer, BiConsumer<F, DateRangePicker> filter) {
    super(dbName, new DateRangePicker(), renderer, filter);
    configure(component);
  }

  public DateDbGridColumn(String dbName, BiConsumer<F, Range<LocalDate>> setter) {
    this(dbName, null, defaultFilter(setter));
  }

}
