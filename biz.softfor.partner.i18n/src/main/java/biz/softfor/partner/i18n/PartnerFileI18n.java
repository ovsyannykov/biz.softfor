package biz.softfor.partner.i18n;

import biz.softfor.i18nutil.I18n;
import static biz.softfor.i18nutil.I18nUtil.UKRAINIAN;
import static biz.softfor.i18nutil.I18nUtil.add;
import biz.softfor.partner.jpa.PartnerFile;
import biz.softfor.partner.jpa.PartnerFile_;
import static java.util.Locale.ENGLISH;
import static java.util.Map.of;

@I18n
public class PartnerFileI18n {

  static {
    add(PartnerFile.TITLE, of(
      ENGLISH, "Partner file"
    , UKRAINIAN, "Файл партнера"
    ));
    add(PartnerFile.TABLE, of(
      ENGLISH, "Partner files"
    , UKRAINIAN, "Файли партнерів"
    ));
    add(PartnerFile_.URI, of(
      ENGLISH, "Link"
    , UKRAINIAN, "Посилання"
    ));
  }

}
