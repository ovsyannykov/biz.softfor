package biz.softfor.vaadin;

import biz.softfor.spring.security.SecurityUtil;
import biz.softfor.vaadin.security.AuthSvc;
import biz.softfor.vaadin.security.LoginView;
import biz.softfor.vaadin.security.ProfileView;
import biz.softfor.vaadin.security.RegistrationView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class MainLayout extends AppLayout implements LocaleChangeObserver {

  public final static String AppTitle = "AppTitle";
  public final static String loginId
  = Text.Login + VaadinUtil.BUTTON_ID_OBJ + VaadinUtil.MAIN_ID_SFX;
  public final static String logoutId
  = Text.Logout + VaadinUtil.BUTTON_ID_OBJ + VaadinUtil.MAIN_ID_SFX;
  public final static String profileId
  = Text.PROFILE + VaadinUtil.BUTTON_ID_OBJ + VaadinUtil.MAIN_ID_SFX;
  public final static String registrationId
  = Text.REGISTRATION + VaadinUtil.BUTTON_ID_OBJ + VaadinUtil.MAIN_ID_SFX;

  private final H3 logo;
  private final LangSelector langCtl;
  private final UserDetailsService userDetailsService;
  private final Button login;
  private final Button registration;

  public MainLayout(
    SideNav appMenu
  , AuthSvc authSvc
  , UserDetailsService userDetailsService
  , I18NProvider i18NProvider
  ) {
    this.userDetailsService = userDetailsService;
    DrawerToggle toggle = new DrawerToggle();
    logo = new H3();
    logo.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.MEDIUM);
    langCtl = new LangSelector(i18NProvider);
    boolean isAuth = SecurityUtil.isAuthorized(SecurityUtil.getAuthentication());
    if(isAuth) {
      login = new Button(new Icon(VaadinIcon.SIGN_OUT), e -> {
        Page page = UI.getCurrent().getPage();
        authSvc.logout();
        page.reload();
      });
      login.setId(logoutId);
      registration = new Button(new Icon(VaadinIcon.USER)
      , e -> VaadinUtil.navigateWithRetPath(ProfileView.PATH));
      registration.setId(profileId);
    } else {
      login = new Button(new Icon(VaadinIcon.SIGN_IN)
      , e -> VaadinUtil.navigateWithRetPath(LoginView.PATH));
      login.setId(loginId);
      registration = new Button(new Icon(VaadinIcon.USER_CARD)
      , e -> VaadinUtil.navigateWithRetPath(RegistrationView.PATH));
      registration.setId(registrationId);
    }
    var header = new HorizontalLayout(toggle, logo, langCtl, login, registration);
    header.add(registration);
    header.setWidthFull();
    header.expand(logo);
    header.addClassNames
    (LumoUtility.Padding.Vertical.NONE, LumoUtility.Padding.Horizontal.MEDIUM);
    header.setAlignItems(FlexComponent.Alignment.CENTER);
    addToNavbar(header);
    Scroller scroller = new Scroller(appMenu);
    scroller.setClassName(LumoUtility.Padding.SMALL);
    addToDrawer(scroller);
    setPrimarySection(Section.DRAWER);
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    logo.setText(getTranslation(AppTitle));
    UserDetails ud = null;
    Authentication auth = SecurityUtil.getAuthentication();
    if(SecurityUtil.isAuthorized(auth)) {
      try {
        ud = userDetailsService.loadUserByUsername(auth.getName());
      } catch(UsernameNotFoundException ex) {}
    }
    if(ud == null) {
      login.setText(getTranslation(Text.Login));
      registration.setText(getTranslation(Text.REGISTRATION));
    } else {
      login.setText(getTranslation(Text.Logout_user, ud.getUsername()));
      registration.setText(getTranslation(Text.PROFILE));
    }
  }

}
