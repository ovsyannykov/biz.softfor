package biz.softfor.partner.i18n;

import biz.softfor.i18nutil.I18n;
import static biz.softfor.i18nutil.I18nUtil.UKRAINIAN;
import static biz.softfor.i18nutil.I18nUtil.add;
import biz.softfor.partner.jpa.LocationType;
import static java.util.Locale.ENGLISH;
import static java.util.Map.of;

@I18n
public class LocationTypeI18n {

  static {
    add(LocationType.TITLE, of(
      ENGLISH, "Location type"
    , UKRAINIAN, "Тип розташування"
    ));
    add(LocationType.TABLE, of(
      ENGLISH, "Location types"
    , UKRAINIAN, "Типи розташувань"
    ));
  }

}
