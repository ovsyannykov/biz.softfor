package biz.softfor.util.i18n;

import biz.softfor.i18ngen.I18n;
import static biz.softfor.i18ngen.I18nGen.UKRAINIAN;
import static biz.softfor.i18ngen.I18nGen.add;
import biz.softfor.util.BooleansEnum;
import static java.util.Locale.ENGLISH;
import static java.util.Map.of;

@I18n
public class BooleansEnumI18n {

  static {
    add(BooleansEnum.No, of(
      ENGLISH, BooleansEnum.No
    , UKRAINIAN, "Ні"
    ));
    add(BooleansEnum.Yes, of(
      ENGLISH, BooleansEnum.Yes
    , UKRAINIAN, "Так"
    ));
  }

}
