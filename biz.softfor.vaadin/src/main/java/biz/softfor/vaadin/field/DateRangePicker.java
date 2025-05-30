package biz.softfor.vaadin.field;

import biz.softfor.util.Range;
import biz.softfor.vaadin.CSS;
import biz.softfor.vaadin.DateI18n;
import biz.softfor.vaadin.DatePickerI18n;
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

  public String getFormat() {
    return to.getI18n().getDateFormats().get(0);
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    from.setPlaceholder(getTranslation(DateI18n.FROM));
    DatePickerI18n.localeChange(from, event);
    to.setPlaceholder(getTranslation(DateI18n.TO));
    DatePickerI18n.localeChange(to, event);
  }

  @Override
  protected void setPresentationValue(Range<LocalDate> dateRange) {
    LocalDate vFrom, vTo;
    if(dateRange == null) {
      vFrom = null;
      vTo = null;
    } else {
      vFrom = dateRange.getFrom();
      vTo = dateRange.getTo();
    }
    from.setValue(vFrom);
    to.setValue(vTo);
  }

}
