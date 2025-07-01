package biz.softfor.vaadin.demo.i18n;

import biz.softfor.i18ngen.I18n;
import static biz.softfor.i18ngen.I18nGen.UKRAINIAN;
import static biz.softfor.i18ngen.I18nGen.add;
import biz.softfor.vaadin.MainLayout;
import biz.softfor.vaadin.security.LoginView;
import static java.util.Locale.ENGLISH;
import static java.util.Map.of;

@I18n
public class DemoI18n {

  static {
    add(MainLayout.AppTitle, of(
      ENGLISH, "Vaadin Demo"
    , UKRAINIAN, "Vaadin демо"
    ));
    add(LoginView.AdditionalInfo, of(
      ENGLISH, "If you have any problems, please contact your administrator by email admin@acme.biz or phone +9(888)-777-66-55."
    , UKRAINIAN, "Якщо у вас виникли проблеми, зверніться до адміністратора за електронною поштою admin@acme.biz або за телефоном +9(888)-777-66-55."
    ));
  }

}
