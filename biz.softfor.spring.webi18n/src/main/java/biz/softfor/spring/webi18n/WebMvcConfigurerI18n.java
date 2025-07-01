package biz.softfor.spring.webi18n;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

@Component
public class WebMvcConfigurerI18n implements WebMvcConfigurer {

  private final LocaleChangeInterceptor localeChangeInterceptor;

  public WebMvcConfigurerI18n(LocaleChangeInterceptor localeChangeInterceptor) {
    this.localeChangeInterceptor = localeChangeInterceptor;
  }

  @Override
  public void addInterceptors(InterceptorRegistry interceptorRegistry) {
    interceptorRegistry.addInterceptor(localeChangeInterceptor);
  }

}
