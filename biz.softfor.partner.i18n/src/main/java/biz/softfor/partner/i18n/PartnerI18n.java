package biz.softfor.partner.i18n;

import biz.softfor.i18nutil.I18n;
import static biz.softfor.i18nutil.I18nUtil.UKRAINIAN;
import static biz.softfor.i18nutil.I18nUtil.add;
import biz.softfor.partner.jpa.Partner;
import biz.softfor.partner.jpa.Partner_;
import static java.util.Locale.ENGLISH;
import static java.util.Map.of;

@I18n
public class PartnerI18n {

  static {
    add(Partner.TITLE, of(
      ENGLISH, "Partner"
    , UKRAINIAN, "Партнер"
    ));
    add(Partner.TABLE, of(
      ENGLISH, "Partners"
    , UKRAINIAN, "Партнери"
    ));
    add(Partner_.PARTNER_NAME, of(
      ENGLISH, "Name"
    , UKRAINIAN, "Найменування/Ім'я"
    ));
    add(Partner.nameNotPerson, of(
      ENGLISH, "Name"
    , UKRAINIAN, "Найменування"
    ));
    add(Partner.namePerson, of(
      ENGLISH, "Name"
    , UKRAINIAN, "Ім'я"
    ));
    add(Partner_.PARTNER_REGDATE, of(
      ENGLISH, "Registration date/Birthdate"
    , UKRAINIAN, "Дата реєстрації/народження"
    ));
    add(Partner.regdateNotPerson, of(
      ENGLISH, "Registration date"
    , UKRAINIAN, "Дата реєстрації"
    ));
    add(Partner.regdatePerson, of(
      ENGLISH, "Birthdate"
    , UKRAINIAN, "Дата народження"
    ));
    add(Partner_.PARTNER_REGCODE, of(
      ENGLISH, "EIN/ITIN"
    , UKRAINIAN, "ЄГРПОУ/ІПН"
    ));
    add(Partner.regcodeNotPerson, of(
      ENGLISH, "EIN"
    , UKRAINIAN, "ЄГРПОУ"
    ));
    add(Partner.regcodePerson, of(
      ENGLISH, "ITIN"
    , UKRAINIAN, "ІПН"
    ));
    add(Partner_.ADDRESS, of(
      ENGLISH, "Address"
    , UKRAINIAN, "Адреса"
    ));
    add(Partner_.PARTNER_FULLNAME, of(
      ENGLISH, "Full name/Last name"
    , UKRAINIAN, "Повне найменування/Прізвище"
    ));
    add(Partner.fullnameNotPerson, of(
      ENGLISH, "Full name"
    , UKRAINIAN, "Повне найменування"
    ));
    add(Partner.fullnamePerson, of(
      ENGLISH, "Surname"
    , UKRAINIAN, "Прізвище"
    ));
    add(Partner_.PARENT, of(
      ENGLISH, "Parent"
    , UKRAINIAN, "Батько"
    ));
    add(Partner.Person_details_must_be_empty_for_the_non_person_partner_type, of(
      ENGLISH, "Person details must be empty for the non person partner type."
    , UKRAINIAN, "Не фізособа не може мати персональні дані."
    ));
  }

}
