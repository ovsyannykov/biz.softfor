package biz.softfor.vaadin.security;

import biz.softfor.vaadin.Text;
import com.vaadin.flow.spring.annotation.SpringComponent;
import jakarta.validation.Validator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RegistrationForm extends ProfileForm {

  public RegistrationForm(
    ProfileFormColumns columns
  , Validator validator
  , PasswordEncoder passwordEncoder
  ) {
    super(Text.REGISTRATION, columns, validator, passwordEncoder);
    password.setRequiredIndicatorVisible(true);
    confirmPassword.setRequiredIndicatorVisible(true);
  }

}
