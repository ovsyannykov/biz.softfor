package biz.softfor.util.i18n;

import biz.softfor.i18ngen.I18n;
import static biz.softfor.i18ngen.I18nGen.UKRAINIAN;
import static biz.softfor.i18ngen.I18nGen.add;
import biz.softfor.util.Constants;
import biz.softfor.util.api.BasicResponse;
import static java.util.Locale.ENGLISH;
import static java.util.Map.of;

@I18n
public class CommonI18n {

  static {
    add(Constants.DESCR, of(
      ENGLISH, "Description"
    , UKRAINIAN, "Опис"
    ));
    add(Constants.DETAILS, of(
      ENGLISH, "Details"
    , UKRAINIAN, "Подробиці"
    ));
    add(Constants.FULLNAME, of(
      ENGLISH, "Full name"
    , UKRAINIAN, "Повне найменування"
    ));
    add(Constants.ID, of(
      ENGLISH, "Id"
    , UKRAINIAN, "Ідентифікатор"
    ));
    add(Constants.LANGUAGE, of(
      ENGLISH, "Language"
    , UKRAINIAN, "Мова"
    ));
    add(Constants.NAME, of(
      ENGLISH, "Name"
    , UKRAINIAN, "Найменування"
    ));
    add(Constants.NOTE, of(
      ENGLISH, "Note"
    , UKRAINIAN, "Примітка"
    ));
    add(Constants.TYP, of(
      ENGLISH, "Type"
    , UKRAINIAN, "Тип"
    ));
    add(BasicResponse.Access_denied, of(
      ENGLISH, "Access denied."
    , UKRAINIAN, "Доступ заборонено."
    ));
    add(BasicResponse.Response_parse_error, of(
      ENGLISH, "Response parse error."
    , UKRAINIAN, "Помилка аналізу відповіді."
    ));
    add("Unsupported_operation", of(
      ENGLISH, "Unsupported operation: {0}."
    , UKRAINIAN, "Непідтримувана операція: {0}."
    ));
  }

}
