package biz.softfor.vaadin.dbgrid;

import biz.softfor.util.Range;
import biz.softfor.util.api.filter.FilterId;
import biz.softfor.vaadin.CSS;
import biz.softfor.vaadin.DateI18n;
import biz.softfor.vaadin.VaadinUtil;
import biz.softfor.vaadin.field.DateRangePicker;
import java.time.LocalDate;
import java.util.function.BiConsumer;
import java.util.function.Function;

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

  public DateDbGridColumn(
    String dbName
  , Function<E, LocalDate> getter
  , BiConsumer<F, DateRangePicker> filter
  , int d
  ) {
    super(dbName, new DateRangePicker(), null, filter);
    configure(component);
    setRenderer(VaadinUtil.defaultRenderer(e -> getter.apply(e)
    .format(DateI18n.getFormatter(component.getUI().get().getLocale()))));
  }

  public DateDbGridColumn(
    String dbName
  , Function<E, LocalDate> getter
  , BiConsumer<F, Range<LocalDate>> setter
  ) {
    this(dbName, getter, defaultFilter(setter), 0);
  }

}
