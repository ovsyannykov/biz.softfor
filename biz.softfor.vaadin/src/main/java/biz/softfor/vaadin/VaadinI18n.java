package biz.softfor.vaadin;

import biz.softfor.spring.messagesi18n.I18n;
import biz.softfor.util.Locales;
import com.vaadin.flow.i18n.I18NProvider;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
public class VaadinI18n implements I18NProvider {

  private final MessageSource ms;

  public VaadinI18n
  (MessageSource ms, @Value(I18n.LOCALES_PROPERTY_DEFAULT) String[] locales) {
    this.ms = ms;
    Locales.init(locales);
  }

  @Override
  public List<Locale> getProvidedLocales() {
    return Locales.get();
  }

  @Override
  public String getTranslation(String key, Locale locale, Object... args) {
    return ms.getMessage(key, args, locale);
  }

}
