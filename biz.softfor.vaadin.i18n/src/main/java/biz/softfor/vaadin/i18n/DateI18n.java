package biz.softfor.vaadin.i18n;

import biz.softfor.i18nutil.I18n;
import static biz.softfor.i18nutil.I18nUtil.UKRAINIAN;
import static biz.softfor.i18nutil.I18nUtil.add;
import biz.softfor.vaadin.DatePickerI18n;
import static java.util.Locale.ENGLISH;
import static java.util.Map.of;

@I18n
public class DateI18n {

  static {
    add(biz.softfor.vaadin.DateI18n.FROM, of(
      ENGLISH, "From"
    , UKRAINIAN, "Від"
    ));
    add(biz.softfor.vaadin.DateI18n.TO, of(
      ENGLISH, "To"
    , UKRAINIAN, "До"
    ));
    add(biz.softfor.vaadin.DateI18n.FORMAT, of(
      ENGLISH, "MM/dd/yyyy"
    , UKRAINIAN, "dd.MM.yyyy"
    ));
    add(biz.softfor.vaadin.DateI18n.TODAY, of(
      ENGLISH, biz.softfor.vaadin.DateI18n.TODAY
    , UKRAINIAN, "Сьогодні"
    ));
    add(DatePickerI18n.DAYS, of(
      ENGLISH, "Sunday,Monday,Tuesday,Wednesday,Thursday,Friday,Saturday"
    , UKRAINIAN, "Неділя,Понеділок,Вівторок,Середа,Четвер,П'ятниця,Субота"
    ));
    add(DatePickerI18n.DAYS_SHORT, of(
      ENGLISH, "Sun,Mon,Tue,Wed,Thu,Fri,Sat"
    , UKRAINIAN, "Нед,Пон,Вів,Сер,Чет,Птн,Суб"
    ));
    add(DatePickerI18n.MONTHS, of(
      ENGLISH, "January,February,March,April,May,June,July,August,September,October,November,December"
    , UKRAINIAN, "Січень,Лютий,Березень,Квітень,Травень,Червень,Липень,Серпень,Вересень,Жовтень,Листопад,Грудень"
    ));
  }
  
}
