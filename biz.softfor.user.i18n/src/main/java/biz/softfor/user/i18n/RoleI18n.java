package biz.softfor.user.i18n;

import biz.softfor.i18nutil.I18n;
import static biz.softfor.i18nutil.I18nUtil.UKRAINIAN;
import static biz.softfor.i18nutil.I18nUtil.add;
import biz.softfor.user.jpa.Role;
import biz.softfor.user.jpa.Role_;
import static java.util.Locale.ENGLISH;
import static java.util.Map.of;

@I18n
public class RoleI18n {

  static {
    add(Role.TITLE, of(
      ENGLISH, "Role"
    , UKRAINIAN, "Роль"
    ));
    add(Role.TABLE, of(
      ENGLISH, "Roles"
    , UKRAINIAN, "Ролі"
    ));
    add("Delete_constraint", of(
      ENGLISH, "You can delete orphan roles only. {0}"
    , UKRAINIAN, "Можна видалити лише вже не потрібні ролі. {0}"
    ));
    add(Role_.DEFAULT_ACCESS, of(
      ENGLISH, "Default access"
    , UKRAINIAN, "Дозвіл за замовчуванням"
    ));
    add(Role_.IS_URL, of(
      ENGLISH, "URL?"
    , UKRAINIAN, "URL?"
    ));
    add(Role_.UPDATE_FOR, of(
      ENGLISH, "For editing?"
    , UKRAINIAN, "Для оновлення?"
    ));
    add(Role_.ORPHAN, of(
      ENGLISH, "Orphan"
    , UKRAINIAN, "Не пов'язаний"
    ));
    add(Role_.DENIED_FOR_ALL, of(
      ENGLISH, "Denied for all"
    , UKRAINIAN, "Заборонено всім"
    ));
    add(Role_.OBJ_NAME, of(
      ENGLISH, "Object"
    , UKRAINIAN, "Об'єкт"
    ));
  }

}
