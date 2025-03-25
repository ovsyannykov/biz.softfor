package biz.softfor.partner.i18n;

import biz.softfor.i18nutil.I18n;
import static biz.softfor.i18nutil.I18nUtil.UKRAINIAN;
import static biz.softfor.i18nutil.I18nUtil.add;
import static java.util.Locale.ENGLISH;
import static java.util.Map.of;

@I18n
public class PersonDetailsI18n {

  static {
    add("personDetails", of(
      ENGLISH, "Personal data"
    , UKRAINIAN, "Персональні дані"
    ));
    add("passportSeries", of(
      ENGLISH, "Passport series"
    , UKRAINIAN, "Серія паспорта"
    ));
    add("passportNumber", of(
      ENGLISH, "Passport number"
    , UKRAINIAN, "Номер паспорта"
    ));
    add("passportDate", of(
      ENGLISH, "Passport date"
    , UKRAINIAN, "Дата видачі паспорта"
    ));
    add("middlename", of(
      ENGLISH, "Middlename"
    , UKRAINIAN, "По батькові"
    ));
    add("passportIssued", of(
      ENGLISH, "Passport issued"
    , UKRAINIAN, "Ким видан"
    ));
    add("married", of(
      ENGLISH, "Married?"
    , UKRAINIAN, "Одружений(-а)?"
    ));
  }

}
