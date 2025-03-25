package biz.softfor.address.i18n;

import biz.softfor.address.jpa.State;
import biz.softfor.i18nutil.I18n;
import static biz.softfor.i18nutil.I18nUtil.UKRAINIAN;
import static biz.softfor.i18nutil.I18nUtil.add;
import static java.util.Locale.ENGLISH;
import static java.util.Map.of;

@I18n
public class StateI18n {

  static {
    add(State.TITLE, of(
      ENGLISH, "State"
    , UKRAINIAN, "Область"
    ));
    add(State.TABLE, of(
      ENGLISH, "States"
    , UKRAINIAN, "Області"
    ));
  }

}
