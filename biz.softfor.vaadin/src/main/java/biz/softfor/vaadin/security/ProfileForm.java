package biz.softfor.vaadin.security;

import biz.softfor.spring.security.SecurityUtil;
import biz.softfor.user.jpa.User;
import biz.softfor.user.jpa.UserWor;
import biz.softfor.user.jpa.User_;
import biz.softfor.vaadin.EntityForm;
import biz.softfor.vaadin.EntityFormColumns;
import biz.softfor.vaadin.Text;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.PasswordField.PasswordFieldI18n;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.spring.annotation.SpringComponent;
import jakarta.validation.Validator;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProfileForm extends EntityForm<Long, User, UserWor> {

  private final PasswordEncoder passwordEncoder;
  protected final PasswordField password;

  protected ProfileForm(
    String title
  , EntityFormColumns columns
  , Validator validator
  , PasswordEncoder passwordEncoder
  ) {
    super(title, columns, validator);
    this.passwordEncoder = passwordEncoder;
    password = (PasswordField)columns.get(User_.PASSWORD);
  }

  @Autowired
  public ProfileForm(
    ProfileFormColumns columns
  , Validator validator
  , PasswordEncoder passwordEncoder
  ) {
    this(Text.PROFILE, columns, validator, passwordEncoder);
    fields.remove(User_.PASSWORD);
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    super.localeChange(event);
    password.setPlaceholder(getTranslation(Text.Enter_new_password));
    password.setHelperText(getTranslation(User.PASSWORD_CONSTRAINTS));
    password.setI18n(
      new PasswordFieldI18n()
      .setMinLengthErrorMessage
      (getTranslation(User.Password_minimum_length, User.PASSWORD_MIN_LENGTH))
      .setMaxLengthErrorMessage
      (getTranslation(User.Password_maximum_length, User.PASSWORD_MAX_LENGTH))
      .setPatternErrorMessage(getTranslation(User.PASSWORD_CONSTRAINT))
    );
  }

  @Override
  protected User onSave(User data) {
    User result = super.onSave(data);
    String pwd = result.getPassword();
    if(pwd != null) {
      result.setPassword(passwordEncoder.encode(pwd));
    }
    return result;
  }

  @Override
  public void setData(User data, boolean isAdd) {
    super.setData(data, isAdd);
    password.setVisible(isAdd || Objects.equals
    (data.getUsername(), SecurityUtil.getAuthentication().getName()));
  }

}
