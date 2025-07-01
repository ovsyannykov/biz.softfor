package biz.softfor.user.i18n;

import biz.softfor.i18ngen.I18n;
import static biz.softfor.i18ngen.I18nGen.UKRAINIAN;
import static biz.softfor.i18ngen.I18nGen.add;
import biz.softfor.user.jpa.Token;
import biz.softfor.user.jpa.Token_;
import static java.util.Locale.ENGLISH;
import static java.util.Map.of;

@I18n
public class TokenI18n {

  static {
    add(Token.TITLE, of(
      ENGLISH, "Token"
    , UKRAINIAN, "Токен"
    ));
    add(Token.TABLE, of(
      ENGLISH, "Tokens"
    , UKRAINIAN, "Токени"
    ));
    add(Token_.IS_REFRESH, of(
      ENGLISH, "Refresh?"
    , UKRAINIAN, "Рефреш?"
    ));
    add(Token_.EXPIRED, of(
      ENGLISH, "Expires"
    , UKRAINIAN, "Спливає"
    ));
  }

}
