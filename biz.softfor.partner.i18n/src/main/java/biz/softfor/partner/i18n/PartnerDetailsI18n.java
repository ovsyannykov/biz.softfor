package biz.softfor.partner.i18n;

import biz.softfor.i18nutil.I18n;
import static biz.softfor.i18nutil.I18nUtil.UKRAINIAN;
import static biz.softfor.i18nutil.I18nUtil.add;
import static java.util.Locale.ENGLISH;
import static java.util.Map.of;

@I18n
public class PartnerDetailsI18n {

  static {
    add("partnerDetails", of(
      ENGLISH, "Partner details"
    , UKRAINIAN, "Деталі про партнера"
    ));
  }

}
