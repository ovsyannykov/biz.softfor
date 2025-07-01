package biz.softfor.spring;

import biz.softfor.spring.messagesi18n.ConfigSpringMessagesI18n;
import biz.softfor.spring.messagesi18n.I18n;
import java.util.Locale;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@SpringBootTest
@TestConfiguration
@ComponentScan
@EnableAutoConfiguration
@ContextConfiguration(classes = { ConfigSpringMessagesI18n.class, LocalValidatorFactoryBean.class })
public class LocaleTest {

  @Autowired
  private I18n i18n;

  @Test
  public void custom() throws Exception {
    LocaleContextHolder.setLocale(Locale.US);
    Assertions.assertThat(i18n.message("username"))
    .isEqualTo("Username");
  }

  @Test
  public void jakartaValidator() throws Exception {
    LocaleContextHolder.setLocale(Locale.US);
    String id = "jakarta.validation.constraints.NotNull.message";
    Assertions.assertThat(i18n.message(id))
    .isEqualTo("must not be null");
  }

  @Test
  public void undefined() throws Exception {
    LocaleContextHolder.setLocale(Locale.US);
    String id = "qqwweerrttyy";
    Assertions.assertThat(i18n.message(id)).isEqualTo(id);
  }

}
