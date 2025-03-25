package biz.softfor.vaadin;

import biz.softfor.vaadin.dbgrid.DbGridColumns;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.sidenav.SideNavItem;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

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

  public static Object menu(
    Supplier<Object> create
  , Consumer<SideNavItem> addToParent
  , Class<? extends Component> parentTarget
  , BiConsumer<Object, SideNavItem> addItem
  , MenuItemData... data
  ) {
    Object item = create.get();
    if(data != null) {
      for(MenuItemData d : data) {
        if(d.columns == null || d.columns.isAccessible()) {
          Supplier<Object> createItem = () -> {
            SideNavItem result = new SideNavItem(d.title);
            if(d.target != null) {
              result.setPath(d.target);
            }
            result.setLabel(result.getTranslation(d.title));
            return result;
          };
          d.navItem = (SideNavItem)menu(
            createItem
          , i -> addItem.accept(item, i)
          , d.target
          , (e, i) -> ((SideNavItem)e).addItem(i)
          , d.subMenu
          );
        }
      }
    }
    if(addToParent != null
    && (parentTarget != null || !((SideNavItem)item).getItems().isEmpty())) {
      addToParent.accept((SideNavItem)item);
    }
    return item;
  }

}
