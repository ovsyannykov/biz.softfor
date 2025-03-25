package biz.softfor.partner.i18n;

import biz.softfor.i18nutil.I18n;
import static biz.softfor.i18nutil.I18nUtil.UKRAINIAN;
import static biz.softfor.i18nutil.I18nUtil.add;
import biz.softfor.partner.jpa.ContactType;
import static java.util.Locale.ENGLISH;
import static java.util.Map.of;

@I18n
public class ContactTypeI18n {

  static {
    add(ContactType.TITLE, of(
      ENGLISH, "Contact type"
    , UKRAINIAN, "Тип контакту"
    ));
    add(ContactType.TABLE, of(
      ENGLISH, "Contact types"
    , UKRAINIAN, "Типи контактів"
    ));
  }

}
