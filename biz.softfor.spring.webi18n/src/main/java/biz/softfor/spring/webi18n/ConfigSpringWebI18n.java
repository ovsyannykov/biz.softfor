package biz.softfor.spring.webi18n;

import biz.softfor.spring.messagesi18n.I18n;
import biz.softfor.util.Locales;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

@Configuration
@ComponentScan
public class ConfigSpringWebI18n implements WebMvcConfigurer {

  public final static String ACCEPT_LANGUAGE = "Accept-Language";

  @Bean
  public LocaleResolver acceptHeaderLocaleResolver
  (@Value(I18n.LOCALES_PROPERTY_DEFAULT) String[] locales) {
    Locales.init(locales);
    AcceptHeaderLocaleResolver result = new AcceptHeaderLocaleResolver();
    result.setDefaultLocale(Locales.defaultLocale());
    result.setSupportedLocales(Locales.get());
    return result;
  }

  @Bean
  public LocaleChangeInterceptor localeChangeInterceptor() {
    return new LocaleChangeInterceptor();
  }

}
