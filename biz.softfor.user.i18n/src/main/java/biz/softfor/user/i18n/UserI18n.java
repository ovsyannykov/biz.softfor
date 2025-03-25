package biz.softfor.user.i18n;

import biz.softfor.i18nutil.I18n;
import static biz.softfor.i18nutil.I18nUtil.UKRAINIAN;
import static biz.softfor.i18nutil.I18nUtil.add;
import biz.softfor.user.jpa.User;
import biz.softfor.user.jpa.User_;
import static java.util.Locale.ENGLISH;
import static java.util.Map.of;

@I18n
public class UserI18n {

  static {
    add(User.TITLE, of(
      ENGLISH, "User"
    , UKRAINIAN, "Користувач"
    ));
    add(User.TABLE, of(
      ENGLISH, "Users"
    , UKRAINIAN, "Користувачі"
    ));
    add(User_.EMAIL, of(
      ENGLISH, "Email"
    , UKRAINIAN, "Ел.пошта"
    ));
    add(User_.PASSWORD, of(
      ENGLISH, "Password"
    , UKRAINIAN, "Пароль"
    ));
    add(User_.USERNAME, of(
      ENGLISH, "Username"
    , UKRAINIAN, "Ім'я користувача"
    ));
    add(User.GROUPS, of(
      ENGLISH, "Groups"
    , UKRAINIAN, "Групи"
    ));
    add(User.PASSWORD_CONSTRAINT, of(
      ENGLISH, "The password must contain at least one letter and one number."
    , UKRAINIAN, "Пароль повинен містити принаймні одну букву та одну цифру."
    ));
    add(User.PASSWORD_CONSTRAINTS, of(
      ENGLISH, "The password must be no more than " + User.PASSWORD_MIN_LENGTH + " and no less than " + User.PASSWORD_MAX_LENGTH + " characters long, and contain at least one letter and one number."
    , UKRAINIAN, "Пароль повинен мати не більше " + User.PASSWORD_MIN_LENGTH + " та не менше " + User.PASSWORD_MAX_LENGTH + " символів, містити принаймні одну букву та одну цифру."
    ));
    add(User.Password_maximum_length, of(
      ENGLISH, "Password maximum length is {0} characters."
    , UKRAINIAN, "Пароль має бути не більше {0} символів."
    ));
    add(User.Password_minimum_length, of(
      ENGLISH, "Password minimum length is {0} characters."
    , UKRAINIAN, "Пароль має бути не менше {0} символів."
    ));
    add(User.User_not_found, of(
      ENGLISH, "User ''{0}'' not found."
    , UKRAINIAN, "Користувача ''{0}'' не знайдено."
    ));
    add(User.User_with_the_given_name_already_exists, of(
      ENGLISH, "User with the given name ''{0}'' already exists."
    , UKRAINIAN, "Користувач із таким іменем ''{0}'' вже існує."
    ));
  }

}
