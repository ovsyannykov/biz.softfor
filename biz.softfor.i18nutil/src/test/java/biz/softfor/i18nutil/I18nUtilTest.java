package biz.softfor.i18nutil;

import static biz.softfor.i18nutil.I18nUtil.UKRAINIAN;
import static biz.softfor.i18nutil.I18nUtil.add;
import biz.softfor.util.Constants;
import static java.util.Locale.ENGLISH;
import static java.util.Map.of;
import org.junit.jupiter.api.Test;

@I18n
public class I18nUtilTest {

  static {
    add(Constants.LANGUAGE, of(
      ENGLISH, "Languag"
    , UKRAINIAN, "Wова"
    ));
    add("biz.softfor.i18nutil.I18nUtilTest.tezzt", of(
      ENGLISH, "I18nUtilTest tezzt"
    ));
    add(ENGLISH.toString(), of(
      ENGLISH, ENGLISH.getDisplayLanguage(ENGLISH)
    , UKRAINIAN, "Англійська"
    ));
  }

  @Test
  public void testMain() throws Exception {
    I18nUtil.genMessages("./bak", this.getClass().getPackageName());
  }

}
