package biz.softfor.address.i18n;

import biz.softfor.address.jpa.District;
import biz.softfor.i18nutil.I18n;
import static biz.softfor.i18nutil.I18nUtil.UKRAINIAN;
import static biz.softfor.i18nutil.I18nUtil.add;
import static java.util.Locale.ENGLISH;
import static java.util.Map.of;

@I18n
public class DistrictI18n {

  static {
    add(District.TITLE, of(
      ENGLISH, "District"
    , UKRAINIAN, "Район"
    ));
    add(District.TABLE, of(
      ENGLISH, "Districts"
    , UKRAINIAN, "Райони"
    ));
  }

}
