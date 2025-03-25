package biz.softfor.partner.i18n;

import biz.softfor.i18nutil.I18n;
import static biz.softfor.i18nutil.I18nUtil.UKRAINIAN;
import static biz.softfor.i18nutil.I18nUtil.add;
import static java.util.Locale.ENGLISH;
import static java.util.Map.of;

@I18n
public class ContactDetailsI18n {

  static {
    add("contactDetails", of(
      ENGLISH, "Contact details"
    , UKRAINIAN, "Деталі контакту"
    ));
  }

}
