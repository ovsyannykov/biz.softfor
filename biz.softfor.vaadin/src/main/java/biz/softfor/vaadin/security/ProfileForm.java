package biz.softfor.vaadin.security;

import biz.softfor.user.jpa.User;
import biz.softfor.user.jpa.UserWor;
import biz.softfor.user.jpa.User_;
import biz.softfor.vaadin.EntityForm;
import biz.softfor.vaadin.Text;
import biz.softfor.vaadin.VaadinUtil;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.PasswordField.PasswordFieldI18n;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.spring.annotation.SpringComponent;
import jakarta.validation.Validator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProfileForm extends EntityForm<Long, User, UserWor> {

  public final static String Passwords_do_not_match = "Passwords_do_not_match";

  private final PasswordEncoder passwordEncoder;
  protected final PasswordField password;
  protected final PasswordField confirmPassword;
  private boolean enablePasswordValidation;

  protected ProfileForm(
    String title
  , ProfileFormColumns columns
  , Validator validator
  , PasswordEncoder passwordEncoder
  ) {
    super(title, columns, validator);
    this.passwordEncoder = passwordEncoder;
    password = (PasswordField)columns.get(User_.PASSWORD);
    VaadinUtil.autocompleteOff(password);
    confirmPassword = new PasswordField(e -> {
      enablePasswordValidation = true;
      binder.validate();
    });
    VaadinUtil.autocompleteOff(confirmPassword);
    propertiesPane.add(confirmPassword);
    binder.forField(password).withValidator(this::passwordValidator)
    .bind(User_.PASSWORD);
  }

  @Autowired
  public ProfileForm(
    ProfileFormColumns columns
  , Validator validator
  , PasswordEncoder passwordEncoder
  ) {
    this(Text.PROFILE, columns, validator, passwordEncoder);
    fields.remove(User_.PASSWORD);
    password.setRequiredIndicatorVisible(false);
    password.addValueChangeListener(e -> confirmPassword
    .setRequiredIndicatorVisible(StringUtils.isNotEmpty(e.getValue())));
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
    confirmPassword.setLabel(getTranslation(Text.Confirm_password));
    confirmPassword.setPlaceholder(getTranslation(Text.Confirm_new_password));
  }

  @Override
  protected User onSave(User data) {
    User result = super.onSave(data);
    String pwd = result.getPassword();
    pwd = StringUtils.isBlank(pwd) ? null : passwordEncoder.encode(pwd);
    result.setPassword(pwd);
    return result;
  }

  @Override
  protected boolean isValid(User data) {
    boolean result = super.isValid(data);
    if(!isAdd()) {
      result &=
      StringUtils.isAllBlank(password.getValue(), confirmPassword.getValue())
      || !password.isInvalid();
    }
    return result;
  }

  private ValidationResult passwordValidator(String pass, ValueContext ctx) {
    ValidationResult result;
    if(!enablePasswordValidation) {
      enablePasswordValidation = true;
      result = ValidationResult.ok();
    } else if(pass != null && pass.equals(confirmPassword.getValue())) {
      result = ValidationResult.ok();
    } else {
      result = ValidationResult.error(getTranslation(Passwords_do_not_match));
    }
    return result;
  }

}
