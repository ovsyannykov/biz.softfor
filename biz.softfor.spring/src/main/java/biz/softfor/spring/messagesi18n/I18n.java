package biz.softfor.spring.messagesi18n;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class I18n {

  public final static String LOCALES_PROPERTY = "biz.softfor.locales";
  public final static String LOCALES_PROPERTY_DEFAULT_VALUE = "en,uk";
  public final static String LOCALES_PROPERTY_DEFAULT
  = "${" + LOCALES_PROPERTY + ":" + LOCALES_PROPERTY_DEFAULT_VALUE + "}";

  @Autowired
  private MessageSource ms;

  public String message(String key, String... args) {
    return ms.getMessage(key, args, LocaleContextHolder.getLocale());
  }

}
