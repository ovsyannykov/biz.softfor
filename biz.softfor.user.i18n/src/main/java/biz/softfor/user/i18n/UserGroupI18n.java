package biz.softfor.user.i18n;

import biz.softfor.i18ngen.I18n;
import static biz.softfor.i18ngen.I18nGen.UKRAINIAN;
import static biz.softfor.i18ngen.I18nGen.add;
import biz.softfor.user.jpa.UserGroup;
import static java.util.Locale.ENGLISH;
import static java.util.Map.of;

@I18n
public class UserGroupI18n {

  static {
    add(UserGroup.TITLE, of(
      ENGLISH, "Group"
    , UKRAINIAN, "Група"
    ));
    add(UserGroup.TABLE, of(
      ENGLISH, "Groups"
    , UKRAINIAN, "Групи"
    ));
  }

}
