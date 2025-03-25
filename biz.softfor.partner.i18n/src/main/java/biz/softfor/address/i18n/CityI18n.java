package biz.softfor.address.i18n;

import biz.softfor.address.jpa.City;
import biz.softfor.i18nutil.I18n;
import static biz.softfor.i18nutil.I18nUtil.UKRAINIAN;
import static biz.softfor.i18nutil.I18nUtil.add;
import static java.util.Locale.ENGLISH;
import static java.util.Map.of;

@I18n
public class CityI18n {

  static {
    add(City.TITLE, of(
      ENGLISH, "City"
    , UKRAINIAN, "Місто"
    ));
    add(City.TABLE, of(
      ENGLISH, "Cities"
    , UKRAINIAN, "Міста"
    ));
  }

}
