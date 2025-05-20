package biz.softfor.playwrightutil;

import biz.softfor.util.api.StdPath;
import biz.softfor.vaadin.GridColumn;
import biz.softfor.vaadin.MainLayout;
import biz.softfor.vaadin.dbgrid.DbGrid;
import biz.softfor.vaadin.field.ToManyField;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import java.util.List;

public class VaadinTestUtil {

  public static Locator.WaitForOptions DETACHED
  = new Locator.WaitForOptions().setState(WaitForSelectorState.DETACHED);
  public static Locator.WaitForOptions HIDDEN
  = new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN);

  public static boolean addRows2GridField(
    Page page
  , String name
  , Class<?> gridClass
  , String fieldFilterName
  , String fieldFilterValue
  , Screenshot screenshot
  ) {
    boolean result = false;
    screenshot.get(page);
    Locator add = page.locator("#" + ToManyField.addId(name));
    add.scrollIntoViewIfNeeded();
    screenshot.get(page);
    add.click();
    String dialogSlctId = ToManyField.gridId(name);
    page.locator("#" + dialogSlctId).waitFor(DriverHlpr.wait);
    screenshot.get(page);
    page.locator("#" + DbGrid.clearId(gridClass)).click();
    Locator fieldFilter = page.locator
    ("//*[@id='" + dialogSlctId + "']/vaadin-grid-cell-content/*[@id='"
    + GridColumn.columnFilterId(fieldFilterName) + "']/input");
    fieldFilter.scrollIntoViewIfNeeded();
    screenshot.get(page);
    fieldFilter.fill(fieldFilterValue);
    screenshot.get(page);
    page.locator("#" + DbGrid.filtrateId(gridClass)).click();
    screenshot.get(page);
    List<Locator> selectRow = page.locator
    ("//*[@id='" + dialogSlctId + "']//vaadin-checkbox").all();
    boolean screenshotDone = false;
    for(Locator sr : selectRow) {
      if(!"selectAllCheckbox".equals(sr.getAttribute("id"))) {
        if(!screenshotDone) {
          sr.scrollIntoViewIfNeeded();
          screenshot.get(page);
          screenshotDone = true;
        }
        if(sr.isVisible()) {
          sr.click();
          result = true;
        } else {
          break;
        }
      }
    }
    screenshot.get(page);
    page.locator("#" + ToManyField.selectId()).click();
    page.locator("#" + dialogSlctId).waitFor
    (new Locator.WaitForOptions().setState(WaitForSelectorState.DETACHED));
    screenshot.get(page);
    return result;
  }

  public static Object highlight(Locator locator) {
    return locator.evaluate
    ("e => {const r=e.style.border; e.style.border='2px solid red'; return r; }");
  }

  public static void unhighlight(Locator locator, Object originalStyle) {
    locator.evaluate("e => e.style.border='" + originalStyle + "'");
  }

  public static void login
  (DriverHlpr drvHlpr, String username, String password, Screenshot screenshot) {
    drvHlpr.page.navigate(StdPath.locationUri(drvHlpr.port, StdPath.LOGIN));
    Locator submit = drvHlpr.page.locator("//vaadin-button[@slot='submit']");
    submit.waitFor(DriverHlpr.lwait);
    drvHlpr.page.locator("//input[@name='username']").fill(username);
    drvHlpr.page.locator("//input[@name='password']").fill(password);
    if(screenshot != null) {
      screenshot.get(drvHlpr.page);
    }
    submit.click();
    drvHlpr.page.locator("#" + MainLayout.logoutId).waitFor(DriverHlpr.lwait);
  }

  public static void login(DriverHlpr drvHlpr, String username, String password) {
    login(drvHlpr, username, password, null);
  }

  public static void logout(Page page) {
    page.locator("#" + MainLayout.logoutId).click();
    page.locator("#" + MainLayout.loginId).waitFor(DriverHlpr.lwait);
  }

  public static Locator sideNavSection(Page page, String name) {
    String sectXp = "//vaadin-side-nav/vaadin-side-nav-item[text()='" + name + "']";
    Locator sectLctr = page.locator(sectXp);
    Locator toggle = sectLctr.locator("button[part='toggle-button']");
    return toggle.all().getFirst();
  }

  public static String gridColumnHeaderXp(String gridId, String column) {
    return "//vaadin-grid[@id='" + gridId + "']/vaadin-grid-cell-content/"
    + "vaadin-grid-sorter[contains(text(),'" + column + "')]";
  }

}
