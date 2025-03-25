package biz.softfor.vaadin.security;

import biz.softfor.util.api.StdPath;
import biz.softfor.vaadin.BasicView;
import biz.softfor.vaadin.MainLayout;
import biz.softfor.vaadin.Text;
import biz.softfor.vaadin.VaadinUtil;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;

@Route(value = StdPath.LOGIN, layout = MainLayout.class)
@AnonymousAllowed
public class LoginView extends BasicView implements BeforeEnterObserver {

  public final static String Username = "Username";
  public final static String Password = "Password";
  public final static String Forgot_password_ = "Forgot_password_";
  public final static String Incorrect_username_or_password
  = "Incorrect_username_or_password";
  public final static String Check_that_the_username_and_password
  = "Check_the_username_and_password";
  public final static String AdditionalInfo = "AdditionalInfo";

  private final LoginForm loginForm;

  public LoginView() {
    super(Text.Login);
    addClassName("login-view");
    setSizeFull();
    loginForm = new LoginForm();
    loginForm.setAction(StdPath.LOGIN);
    add(loginForm);
    setAlignSelf(Alignment.CENTER, loginForm);
  }

  @Override
  public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
    Map<String, List<String>> params
    = beforeEnterEvent.getLocation().getQueryParameters().getParameters();
    if(params.containsKey(StdPath.ERROR)) {
      loginForm.setError(true);
    } else {
      List<String> retPath = params.get(VaadinUtil.RETPATH);
      if(CollectionUtils.isNotEmpty(retPath)) {
        loginForm.setAction(StdPath.LOGIN + "?" + VaadinUtil.RETPATH
        + "=" + retPath.getFirst());
      }
    }
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    super.localeChange(event);
    LoginI18n loginI18n = LoginI18n.createDefault();
    LoginI18n.Header i18nHeader = new LoginI18n.Header();
    i18nHeader.setTitle(getTranslation(MainLayout.AppTitle));
    //i18nHeader.setDescription("Application description");
    loginI18n.setHeader(i18nHeader);
    LoginI18n.Form i18nForm = loginI18n.getForm();
    i18nForm.setTitle(getTranslation(Text.Login));
    i18nForm.setUsername(getTranslation(Username));
    i18nForm.setPassword(getTranslation(Password));
    i18nForm.setSubmit(getTranslation(Text.Login));
    i18nForm.setForgotPassword(getTranslation(Forgot_password_));
    loginI18n.setForm(i18nForm);
    LoginI18n.ErrorMessage i18nErrorMessage = loginI18n.getErrorMessage();
    i18nErrorMessage.setTitle(getTranslation(Incorrect_username_or_password));
    i18nErrorMessage.setMessage(getTranslation(Check_that_the_username_and_password));
    loginI18n.setErrorMessage(i18nErrorMessage);
    String addInfo = getTranslation(AdditionalInfo);
    loginI18n.setAdditionalInformation(addInfo.equals(AdditionalInfo) ? "" : addInfo);
    loginForm.setI18n(loginI18n);
  }

}
