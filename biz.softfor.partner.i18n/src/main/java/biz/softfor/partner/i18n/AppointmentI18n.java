package biz.softfor.partner.i18n;

import biz.softfor.i18nutil.I18n;
import static biz.softfor.i18nutil.I18nUtil.UKRAINIAN;
import static biz.softfor.i18nutil.I18nUtil.add;
import biz.softfor.partner.jpa.Appointment;
import static java.util.Locale.ENGLISH;
import static java.util.Map.of;

@I18n
public class AppointmentI18n {

  static {
    add(Appointment.TITLE, of(
      ENGLISH, "Appointment"
    , UKRAINIAN, "Посада"
    ));
    add(Appointment.TABLE, of(
      ENGLISH, "Appointments"
    , UKRAINIAN, "Посади"
    ));
  }

}
