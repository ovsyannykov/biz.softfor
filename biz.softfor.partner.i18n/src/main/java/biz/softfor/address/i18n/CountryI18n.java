package biz.softfor.address.i18n;

import biz.softfor.address.jpa.Country;
import biz.softfor.i18ngen.I18n;
import static biz.softfor.i18ngen.I18nGen.UKRAINIAN;
import static biz.softfor.i18ngen.I18nGen.add;
import static java.util.Locale.ENGLISH;
import static java.util.Map.of;

@I18n
public class CountryI18n {

  static {
    add(Country.TITLE, of(
      ENGLISH, "Country"
    , UKRAINIAN, "Країна"
    ));
    add(Country.TABLE, of(
      ENGLISH, "Countries"
    , UKRAINIAN, "Країни"
    ));
  }

}
