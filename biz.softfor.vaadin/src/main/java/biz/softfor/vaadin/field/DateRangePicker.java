package biz.softfor.vaadin.field;

import biz.softfor.util.Range;
import biz.softfor.vaadin.CSS;
import biz.softfor.vaadin.Text;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datepicker.DatePickerVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import java.time.LocalDate;

public class DateRangePicker extends CustomField<Range<LocalDate>>
implements LocaleChangeObserver {

  private DatePicker from;
  private DatePicker to;

  public DateRangePicker(String label) {
    this();
    setLabel(label);
  }

  public DateRangePicker() {
    from = new DatePicker();
    from.setClearButtonVisible(true);
    from.addThemeVariants(DatePickerVariant.LUMO_SMALL);
    to = new DatePicker();
    to.setClearButtonVisible(true);
    to.addThemeVariants(DatePickerVariant.LUMO_SMALL);
    VerticalLayout layout = new VerticalLayout(from, to);
    layout.setMargin(false);
    layout.setPadding(false);
    layout.setSpacing(false);
    layout.getThemeList().add(CSS.LAYOUT_SPACING_EXTRASMALL);
    add(layout);
  }

  @Override
  protected Range<LocalDate> generateModelValue() {
    return new Range<>(from.getValue(), to.getValue());
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    from.setPlaceholder(getTranslation(Text.Date_from));
    to.setPlaceholder(getTranslation(Text.Date_to));
  }

  @Override
  protected void setPresentationValue(Range<LocalDate> dateRange) {
    from.setValue(dateRange.getFrom());
    to.setValue(dateRange.getTo());
  }

}
