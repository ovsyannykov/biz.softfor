package biz.softfor.util.partner.i18n;

import biz.softfor.i18nutil.I18n;
import static biz.softfor.i18nutil.I18nUtil.UKRAINIAN;
import static biz.softfor.i18nutil.I18nUtil.add;
import biz.softfor.util.partner.PartnerType;
import static java.util.Locale.ENGLISH;
import static java.util.Map.of;

@I18n
public class PartnerTypeI18n {

  static {
    add(PartnerType.PERSON.label, of(
      ENGLISH, PartnerType.PERSON.label
    , UKRAINIAN, "Фізична особа"
    ));
    add(PartnerType.EMPLOYEE.label, of(
      ENGLISH, PartnerType.EMPLOYEE.label
    , UKRAINIAN, "Співробітник"
    ));
    add(PartnerType.DEPARTMENT.label, of(
      ENGLISH, PartnerType.DEPARTMENT.label
    , UKRAINIAN, "Підрозділ"
    ));
    add(PartnerType.LEGAL_ENTITY.label, of(
      ENGLISH, "Legal entity"
    , UKRAINIAN, "Юридична особа"
    ));
    add(PartnerType.ORGANIZATION.label, of(
      ENGLISH, PartnerType.ORGANIZATION.label
    , UKRAINIAN, "Організація"
    ));
  }

}
