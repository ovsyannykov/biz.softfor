package biz.softfor.vaadin.security;

import biz.softfor.user.jpa.User;
import biz.softfor.user.jpa.UserRequest;
import biz.softfor.user.jpa.UserWor;
import biz.softfor.user.spring.UserSvc;
import biz.softfor.util.api.CommonResponse;
import biz.softfor.util.api.StdPath;
import biz.softfor.vaadin.BasicView;
import biz.softfor.vaadin.CSS;
import biz.softfor.vaadin.EntityForm;
import biz.softfor.vaadin.MainLayout;
import biz.softfor.vaadin.Text;
import biz.softfor.vaadin.VaadinUtil;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route(value = StdPath.REGISTRATION, layout = MainLayout.class)
@AnonymousAllowed
public class RegistrationView extends BasicView {

  private final UserSvc service;

  public RegistrationView(RegistrationForm form, UserSvc service) {
    super(Text.REGISTRATION);
    this.service = service;
    add(form);
    addClassName(CSS.EDIT_FORM_EDITING);
    form.addSaveListener(this::save);
    form.addCancelListener(e -> VaadinUtil.returnToRetPath());
    form.setData(new User(), true);
  }

  private void save(EntityForm.SaveEvent e) {
    User item = (User)e.item;
    UserWor itemWor = new UserWor(item);
    UserRequest.Create request = new UserRequest.Create(itemWor);
    CommonResponse<UserWor> response = service.create(request);
    if(response.isOk()) {
      VaadinUtil.returnToRetPath();
    } else {
      VaadinUtil.messageDialog(getTranslation(Text.Save), response.getDescr());
    }
  }

}
