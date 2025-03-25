package biz.softfor.vaadin.security;

import biz.softfor.user.jpa.User_;
import biz.softfor.vaadin.Text;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.spring.annotation.SpringComponent;
import jakarta.validation.Validator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RegistrationForm extends ProfileForm {

  public final static String Passwords_do_not_match = "Passwords_do_not_match";

  private final PasswordField confirmPassword;
  private boolean enablePasswordValidation;

  public RegistrationForm(
    ProfileFormColumns columns
  , Validator validator
  , PasswordEncoder passwordEncoder
  ) {
    super(Text.REGISTRATION, columns, validator, passwordEncoder);
    binder.forField(password).withValidator(this::passwordValidator)
    .bind(User_.PASSWORD);
    confirmPassword = new PasswordField();
    confirmPassword.setRequiredIndicatorVisible(true);
    propertiesPane.add(confirmPassword);
    confirmPassword.addValueChangeListener(e -> {
      enablePasswordValidation = true;
      binder.validate();
    });
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    super.localeChange(event);
    confirmPassword.setLabel(getTranslation(Text.Confirm_password));
    confirmPassword.setPlaceholder(getTranslation(Text.Confirm_new_password));
  }

  private ValidationResult passwordValidator(String pass, ValueContext ctx) {
    if(!enablePasswordValidation) {
      enablePasswordValidation = true;
      return ValidationResult.ok();
    }
    String confPass = confirmPassword.getValue();
    if(pass != null && pass.equals(confPass)) {
      return ValidationResult.ok();
    }
    return ValidationResult.error(getTranslation(Passwords_do_not_match));
  }

}
