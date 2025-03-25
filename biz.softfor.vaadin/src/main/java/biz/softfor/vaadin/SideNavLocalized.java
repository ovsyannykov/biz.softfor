package biz.softfor.vaadin;

import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SideNavLocalized extends SideNav implements LocaleChangeObserver {

  private final MenuItemData[] menuItemData;

  public SideNavLocalized(MenuItemData... menuItemData) {
    this.menuItemData = menuItemData;
    MenuItemData.menu(
      () -> this
    , null
    , null
    , (e, i) -> ((SideNav)e).addItem(i)
    , menuItemData
    );
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    MenuItemData.localeChange(menuItemData);
  }

}
