package biz.softfor.address.i18n;

import biz.softfor.address.jpa.Postcode;
import biz.softfor.i18nutil.I18n;
import static biz.softfor.i18nutil.I18nUtil.UKRAINIAN;
import static biz.softfor.i18nutil.I18nUtil.add;
import static java.util.Locale.ENGLISH;
import static java.util.Map.of;

@I18n
public class PostcodeI18n {

  static {
    add(Postcode.TITLE, of(
      ENGLISH, "Postcode"
    , UKRAINIAN, "Індекс"
    ));
    add(Postcode.TABLE, of(
      ENGLISH, "Postcodes"
    , UKRAINIAN, "Поштові індекси"
    ));
  }

}
