package biz.softfor.vaadin.security;

import biz.softfor.user.jpa.User;
import biz.softfor.user.jpa.UserWor;
import biz.softfor.user.jpa.User_;
import biz.softfor.vaadin.EntityFormColumns;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.util.LinkedHashMap;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProfileFormColumns extends EntityFormColumns {

  public final static LinkedHashMap<String, Component> columns() {
    return new LinkedHashMap<String, Component>() {{
      EmailField email = new EmailField(User_.EMAIL);
      email.setRequiredIndicatorVisible(true);
      put(User_.EMAIL, email);
      put(User_.USERNAME, new TextField(User_.USERNAME));
      PasswordField passwordField = new PasswordField(User_.PASSWORD);
      passwordField.setRequiredIndicatorVisible(true);
      passwordField.setMaxLength(User.PASSWORD_MAX_LENGTH);
      passwordField.setMinLength(User.PASSWORD_MIN_LENGTH);
      passwordField.setPattern(User.PASSWORD_PATTERN);
      put(User_.PASSWORD, passwordField);
    }};
  }

  public ProfileFormColumns() {
    super(columns(), User.class, UserWor.class);
  }

}
