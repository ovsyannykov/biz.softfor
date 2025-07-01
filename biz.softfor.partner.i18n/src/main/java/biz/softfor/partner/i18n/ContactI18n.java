package biz.softfor.partner.i18n;

import biz.softfor.i18ngen.I18n;
import static biz.softfor.i18ngen.I18nGen.UKRAINIAN;
import static biz.softfor.i18ngen.I18nGen.add;
import biz.softfor.partner.jpa.Contact;
import biz.softfor.partner.jpa.Contact_;
import static java.util.Locale.ENGLISH;
import static java.util.Map.of;

@I18n
public class ContactI18n {

  static {
    add(Contact.TITLE, of(
      ENGLISH, "Contact"
    , UKRAINIAN, "Контакт"
    ));
    add(Contact.TABLE, of(
      ENGLISH, "Contacts"
    , UKRAINIAN, "Контакти"
    ));
    add(Contact_.IS_PUBLIC, of(
      ENGLISH, "Public?"
    , UKRAINIAN, "Відкритий?"
    ));
  }

}
