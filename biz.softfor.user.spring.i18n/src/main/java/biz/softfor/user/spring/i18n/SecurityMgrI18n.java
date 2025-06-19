package biz.softfor.user.spring.i18n;

import biz.softfor.i18nutil.I18n;
import static biz.softfor.i18nutil.I18nUtil.UKRAINIAN;
import static biz.softfor.i18nutil.I18nUtil.add;
import biz.softfor.user.spring.SecurityMgr;
import static java.util.Locale.ENGLISH;
import static java.util.Map.of;

@I18n
public class SecurityMgrI18n {

  static {
    add(SecurityMgr.Access_to_fields_denied, of(
      ENGLISH, "Access to the {0} fields denied."
    , UKRAINIAN, "Доступ до полів {0} заборонено."
    ));
    add(SecurityMgr.Access_to_items_denied, of(
      ENGLISH, "Access to ''{0}'' items denied."
    , UKRAINIAN, "Доступ до методу {0} заборонено."
    ));
    add(SecurityMgr.Access_to_method_denied, of(
      ENGLISH, "Access to method ''{0}'' denied: "
    , UKRAINIAN, "Доступ до методу {0} заборонено."
    ));
    add(SecurityMgr.Fields_contains_empty_item, of(
      ENGLISH, "The ''{0}'' field contains empty item(s): {1}."
    , UKRAINIAN, "Поле ''{0}'' містить порожні елементи: {1}."
    ));
    add(SecurityMgr.Fields_contains_invalid_item, of(
      ENGLISH, "The ''{0}'' field contains invalid item ''{1}'' with the ''{2}'' element."
    , UKRAINIAN, "Поле ''{0}'' містить недійсний елемент ''{1}'' з елементом ''{2}''."
    ));
    add(SecurityMgr.Fields_contains_invalid_items, of(
      ENGLISH, "The ''{0}'' field contains invalid item(s): {1}."
    , UKRAINIAN, "Поле ''{0}'' містить недійсний(і) елемент(и): {1}."
    ));
    add(SecurityMgr.Fields_contains_not_plain_column, of(
      ENGLISH, "The ''{0}'' field contains not plain column(s): {1}."
    , UKRAINIAN, "Поле ''{0}'' містить не прості стовпці: {1}."
    ));
  }

}
