package biz.softfor.address.i18n;

import biz.softfor.address.jpa.CityType;
import biz.softfor.i18nutil.I18n;
import static biz.softfor.i18nutil.I18nUtil.UKRAINIAN;
import static biz.softfor.i18nutil.I18nUtil.add;
import static java.util.Locale.ENGLISH;
import static java.util.Map.of;

@I18n
public class CityTypeI18n {

  static {
    add(CityType.TITLE, of(
      ENGLISH, "City type"
    , UKRAINIAN, "Тип нас.пункту"
    ));
    add(CityType.TABLE, of(
      ENGLISH, "City types"
    , UKRAINIAN, "Типи нас.пунктів"
    ));
  }

}
