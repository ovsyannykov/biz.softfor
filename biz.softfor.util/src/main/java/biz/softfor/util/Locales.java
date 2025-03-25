package biz.softfor.util;

import java.util.List;
import java.util.Locale;

public class Locales {

  private static List<Locale> locales;

  public static void init(String[] locales) {
    Locale[] ls = new Locale[locales.length];
    for(int i = 0; i < locales.length; ++i) {
      ls[i] = Locale.of(locales[i]);
    }
    Locales.locales = List.of(ls);
  }

  public static Locale defaultLocale() {
    return locales.getFirst();
  }

  public static List<Locale> get() {
    return locales;
  }

  public static Locale supported(Locale locale) {
    Locale result;
    if(locales.contains(locale)) {
      result = locale;
    } else {
      result = defaultLocale();
    }
    return result;
  }

}
