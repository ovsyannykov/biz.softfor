package biz.softfor.vaadin.security;

import biz.softfor.jpa.crud.querygraph.ColumnDescr;
import biz.softfor.jpa.crud.querygraph.DiffContext;
import biz.softfor.spring.security.SecurityUtil;
import biz.softfor.user.jpa.User;
import biz.softfor.user.jpa.UserRequest;
import biz.softfor.user.jpa.UserWor;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.user.spring.UserSvc;
import biz.softfor.util.api.CommonResponse;
import biz.softfor.util.api.ServerError;
import biz.softfor.vaadin.BasicView;
import biz.softfor.vaadin.CSS;
import biz.softfor.vaadin.EntityForm;
import biz.softfor.vaadin.MainLayout;
import biz.softfor.vaadin.Text;
import biz.softfor.vaadin.VaadinUtil;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import jakarta.annotation.security.PermitAll;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Route(value = ProfileView.PATH, layout = MainLayout.class)
@PermitAll
public class ProfileView extends BasicView {

  public final static String PATH = Text.PROFILE;

  private final SecurityMgr securityMgr;
  private final UserSvc service;

  public ProfileView
  (SecurityMgr securityMgr, ProfileForm profileForm, UserSvc service) {
    super(Text.PROFILE);
    this.securityMgr = securityMgr;
    this.service = service;
    String username = SecurityUtil.getAuthentication().getName();
    UserRequest.Read request = new UserRequest.Read();
    request.filter.setUsername(username);
    request.fields = profileForm.fields;
    CommonResponse<User> response = service.read(request);
    if(CollectionUtils.isEmpty(response.getData())) {
      ConfirmDialog dialog = new ConfirmDialog();
      dialog.setHeader(getTranslation(Text.Not_found));
      dialog.setText(getTranslation(Text.The_requested_item_not_found));
      dialog.setConfirmText(getTranslation(Text.OK));
      dialog.open();
    } else {
      add(profileForm);
      addClassName(CSS.EDIT_FORM_EDITING);
      profileForm.addSaveListener(this::save);
      profileForm.addCancelListener(e -> VaadinUtil.returnToRetPath());
      profileForm.setData(response.getData(0), false);
    }
  }

  private void save(EntityForm.SaveEvent e) {
    User item = (User)e.item;
    try {
      UserWor itemWor = new UserWor(item);
      UserWor oldItemWor = new UserWor((User)e.oldItem);
      DiffContext diffCtx
      = ColumnDescr.diff("", UserWor.class, itemWor, oldItemWor);
      if(diffCtx.changed) {
        UserRequest.Update request = new UserRequest.Update();
        request.data = (UserWor)diffCtx.data;
        request.fields = diffCtx.updateToNull;
        request.filter.assignId(itemWor.getId());
        securityMgr.updateCheck
        (User.class, UserWor.class, request, SecurityUtil.groups());
        CommonResponse<UserWor> response = service.update(request);
        if(response.isOk()) {
          VaadinUtil.returnToRetPath();
        } else {
          VaadinUtil.messageDialog(getTranslation(Text.Save), response.getDescr());
        }
      }
    }
    catch(IllegalAccessException | IllegalArgumentException
    | InstantiationException | InvocationTargetException
    | NoSuchMethodException ex) {
      throw new ServerError(ex);
    }
  }

}
