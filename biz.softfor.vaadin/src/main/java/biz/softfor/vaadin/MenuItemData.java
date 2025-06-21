package biz.softfor.vaadin;

import biz.softfor.vaadin.dbgrid.DbGridColumns;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.sidenav.HasSideNavItems;
import com.vaadin.flow.component.sidenav.SideNavItem;

public class MenuItemData {

  private final String title;
  private final Class<? extends Component> target;
  private final DbGridColumns columns;
  private final MenuItemData[] subMenu;
  private SideNavItem navItem;

  private MenuItemData(
    String title
  , Class<? extends Component> target
  , DbGridColumns columns
  , MenuItemData... subMenu
  ) {
    this.title = title;
    this.target = target;
    this.columns = columns;
    this.subMenu = subMenu;
  }

  public MenuItemData(
    Class<? extends Component> target
  , DbGridColumns columns
  , MenuItemData... subMenu
  ) {
    this(columns.title, target, columns, subMenu);
  }

  public MenuItemData(String title, MenuItemData... subMenu) {
    this(title, null, null, subMenu);
  }

  public MenuItemData
  (String title, Class<? extends Component> target, MenuItemData... subMenu) {
    this(title, target, null, subMenu);
  }

  public static void localeChange(MenuItemData[] data) {
    for(MenuItemData d : data) {
      if(d.navItem != null) {
        d.navItem.setLabel(d.navItem.getTranslation(d.title));
        if(d.subMenu != null) {
          localeChange(d.subMenu);
        }
      }
    }
  }

  public static HasSideNavItems menu(
    HasSideNavItems item
  , HasSideNavItems parent
  , Class<? extends Component> parentTarget
  , MenuItemData... data
  ) {
    if(data != null) {
      for(MenuItemData d : data) {
        if(d.columns == null || d.columns.isAccessible()) {
          SideNavItem subItem = new SideNavItem(d.title);
          if(d.target != null) {
            subItem.setPath(d.target);
          }
          subItem.setLabel(subItem.getTranslation(d.title));
          d.navItem = (SideNavItem)menu(subItem, item, d.target, d.subMenu);
        }
      }
    }
    if(parent != null && (parentTarget != null || !item.getItems().isEmpty())) {
      parent.addItem((SideNavItem)item);
    }
    return item;
  }

}
