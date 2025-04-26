package biz.softfor.vaadin;

import biz.softfor.util.Locales;
import com.vaadin.flow.i18n.I18NProvider;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class DateI18n {

  public final static String FORMAT = "Date_format";
  public final static String FROM = "Date_from";
  public final static String TO = "Date_to";
  public final static String TODAY = "Today";

  private static Map<Locale, DateTimeFormatter> data;

  public DateI18n(I18NProvider i18NProvider) {
    data = new HashMap<>(Locales.get().size());
    for(Locale l : Locales.get()) {
      data.put
      (l, DateTimeFormatter.ofPattern(i18NProvider.getTranslation(FORMAT, l)));
    }
  }

  public static DateTimeFormatter getFormatter(Locale l) {
    return data.get(l);
  }

}
