package biz.softfor.vaadin;

import biz.softfor.util.Locales;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class DatePickerI18n {

  public final static String DAYS = "DatePicker_days";
  public final static String DAYS_SHORT = "DatePicker_days_short";
  public final static String MONTHS = "DatePicker_months";
  public final static String TODAY = "Today";

  private static Map<Locale, DatePicker.DatePickerI18n> data;

  public DatePickerI18n(I18NProvider i18NProvider) {
    if(data == null) {
      data = new HashMap<>(Locales.get().size());
      for(Locale l : Locales.get()) {
        DatePicker.DatePickerI18n d = new DatePicker.DatePickerI18n();
        d.setWeekdays(List.of(i18NProvider.getTranslation(DAYS, l).split(",")));
        d.setWeekdaysShort(List.of(i18NProvider.getTranslation(DAYS_SHORT, l).split(",")));
        d.setMonthNames(List.of(i18NProvider.getTranslation(MONTHS, l).split(",")));
        d.setToday(i18NProvider.getTranslation(TODAY, l));
        d.setCancel(i18NProvider.getTranslation(Text.Cancel, l));
        data.put(l, d);
      }
    }
  }

  public static void localeChange(DatePicker c, LocaleChangeEvent event) {
    c.setI18n(data.get(event.getLocale()));
  }

}
