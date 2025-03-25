package biz.softfor.util.security.i18n;

import biz.softfor.i18nutil.I18n;
import static biz.softfor.i18nutil.I18nUtil.UKRAINIAN;
import static biz.softfor.i18nutil.I18nUtil.add;
import biz.softfor.util.security.DefaultAccess;
import static java.util.Locale.ENGLISH;
import static java.util.Map.of;

@I18n
public class DefaultAccessI18n {

  static {
    add(DefaultAccess.EVERYBODY.label, of(
      ENGLISH, DefaultAccess.EVERYBODY.label
    , UKRAINIAN, "Всі"
    ));
    add(DefaultAccess.AUTHORIZED.label, of(
      ENGLISH, DefaultAccess.AUTHORIZED.label
    , UKRAINIAN, "Авторизовані"
    ));
    add(DefaultAccess.NOBODY.label, of(
      ENGLISH, DefaultAccess.NOBODY.label
    , UKRAINIAN, "Ніхто"
    ));
  }

}
