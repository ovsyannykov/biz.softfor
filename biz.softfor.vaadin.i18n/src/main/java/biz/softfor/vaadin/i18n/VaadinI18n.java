package biz.softfor.vaadin.i18n;

import biz.softfor.i18nutil.I18n;
import static biz.softfor.i18nutil.I18nUtil.UKRAINIAN;
import static biz.softfor.i18nutil.I18nUtil.add;
import biz.softfor.vaadin.Text;
import biz.softfor.vaadin.security.LoginView;
import biz.softfor.vaadin.security.RegistrationForm;
import static java.util.Locale.ENGLISH;
import static java.util.Map.of;

@I18n
public class VaadinI18n {

  static {
    add(Text.Add, of(
      ENGLISH, Text.Add
    , UKRAINIAN, "Додати"
    ));
    add(Text.Administration, of(
      ENGLISH, Text.Administration
    , UKRAINIAN, "Адміністрування"
    ));
    add(Text.Apply, of(
      ENGLISH, Text.Apply
    , UKRAINIAN, "Застосувати"
    ));
    add(Text.Cancel, of(
      ENGLISH, Text.Cancel
    , UKRAINIAN, "Скасувати"
    ));
    add(Text.Clear, of(
      ENGLISH, Text.Clear
    , UKRAINIAN, "Очистити"
    ));
    add(Text.Confirm_new_password, of(
      ENGLISH, "Confirm new password"
    , UKRAINIAN, "Підтвердьте новий пароль"
    ));
    add(Text.Confirm_password, of(
      ENGLISH, "Confirm password"
    , UKRAINIAN, "Підтвердьте пароль"
    ));
    add(Text.Delete, of(
      ENGLISH, Text.Delete
    , UKRAINIAN, "Видалити"
    ));
    add(Text.Edit, of(
      ENGLISH, Text.Edit
    , UKRAINIAN, "Редагувати"
    ));
    add(Text.Enter_new_password, of(
      ENGLISH, "Enter new password"
    , UKRAINIAN, "Введіть новий пароль"
    ));
    add(Text.Filter_by, of(
      ENGLISH, "Filter by {0}..."
    , UKRAINIAN, "Фільтрувати за"
    ));
    add(Text.Filtrate, of(
      ENGLISH, Text.Filtrate
    , UKRAINIAN, "Фільтрувати"
    ));
    add(Text.Logout, of(
      ENGLISH, "Log out"
    , UKRAINIAN, "Вихід"
    ));
    add(Text.Logout_user, of(
      ENGLISH, "Log out {0}"
    , UKRAINIAN, "Вихід {0}"
    ));
    add(Text.Not_found, of(
      ENGLISH, "Not found"
    , UKRAINIAN, "Не знайдено"
    ));
    add(Text.The_requested_item_not_found, of(
      ENGLISH, "The requested item not found!"
    , UKRAINIAN, "Потрібний запис не знайдено!"
    ));
    add(Text.OK, of(
      ENGLISH, Text.OK
    , UKRAINIAN, "Так"
    ));
    add(RegistrationForm.Passwords_do_not_match, of(
      ENGLISH, "Passwords do not match"
    , UKRAINIAN, "Паролі не співпадають"
    ));
    add(Text.PROFILE, of(
      ENGLISH, "Profile"
    , UKRAINIAN, "Профіль"
    ));
    add(Text.REGISTRATION, of(
      ENGLISH, "Registration"
    , UKRAINIAN, "Реєстрація"
    ));
    add(Text.Remove, of(
      ENGLISH, Text.Remove
    , UKRAINIAN, "Видалити"
    ));
    add(Text.Save, of(
      ENGLISH, Text.Save
    , UKRAINIAN, "Зберегти"
    ));
    add(Text.Select, of(
      ENGLISH, Text.Select
    , UKRAINIAN, "Вибрати"
    ));
    add(Text.Submit, of(
      ENGLISH, Text.Submit
    , UKRAINIAN, "Відправити"
    ));
    add(Text.View, of(
      ENGLISH, Text.View
    , UKRAINIAN, "Дивитись"
    ));
    add(Text.Login, of(
      ENGLISH, Text.Login
    , UKRAINIAN, "Вхід"
    ));
    add(LoginView.Username, of(
      ENGLISH, LoginView.Username
    , UKRAINIAN, "Ім'я користувача"
    ));
    add(LoginView.Password, of(
      ENGLISH, LoginView.Password
    , UKRAINIAN, "Пароль"
    ));
    add(LoginView.Forgot_password_, of(
      ENGLISH, "Forgot password?"
    , UKRAINIAN, "Забули пароль?"
    ));
    add(LoginView.Incorrect_username_or_password, of(
      ENGLISH, "Incorrect username or password."
    , UKRAINIAN, "Невірне ім'я користувача або пароль."
    ));
    add(LoginView.Check_that_the_username_and_password, of(
      ENGLISH, "Check that the username and password are correct and try again."
    , UKRAINIAN, "Перевірте ім'я користувача, пароль та повторіть спробу."
    ));
    add(Text.Page_not_found, of(
      ENGLISH, "The requested page not found."
    , UKRAINIAN, "Запитана сторінка не знайдена."
    ));
  }

}
