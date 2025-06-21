package biz.softfor.seleniumutil;

import biz.softfor.util.api.StdPath;
import biz.softfor.vaadin.GridColumn;
import biz.softfor.vaadin.MainLayout;
import biz.softfor.vaadin.dbgrid.DbGrid;
import biz.softfor.vaadin.field.ToManyField;
import java.io.IOException;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

public class VaadinTestUtil {

  public static boolean addRows2GridField(
    DriverHlpr drvHlpr
  , String name
  , Class<?> gridClass
  , String fieldFilterName
  , String fieldFilterValue
  , Screenshot screenshot
  ) throws IOException {
    boolean result = false;
    screenshot.get();
    WebElement add = drvHlpr.driver.findElement(By.id(ToManyField.addId(name)));
    drvHlpr.scrollIntoView(add);
    screenshot.get();
    add.click();
    String dialogSlctId = ToManyField.gridId(name);
    drvHlpr.wait.until(drv -> !drv.findElements(By.id(dialogSlctId)).isEmpty());
    screenshot.get();
    drvHlpr.driver.findElement(By.id(DbGrid.clearId(gridClass))).click();
    WebElement fieldFilter = drvHlpr.driver.findElement
    (By.xpath("//*[@id='" + dialogSlctId + "']/vaadin-grid-cell-content/*[@id='"
    + GridColumn.columnFilterId(fieldFilterName) + "']/input"));
    drvHlpr.scrollIntoView(fieldFilter);
    screenshot.get();
    fieldFilter.sendKeys(fieldFilterValue);
    screenshot.get();
    drvHlpr.driver.findElement(By.id(DbGrid.filtrateId(gridClass))).click();
    screenshot.get();
    List<WebElement> selectRow = drvHlpr.driver.findElements
    (By.xpath("//*[@id='" + dialogSlctId + "']//vaadin-checkbox"));
    boolean screenshotDone = false;
    for(WebElement sr : selectRow) {
      if(!"selectAllCheckbox".equals(sr.getDomAttribute("id"))) {
        if(!screenshotDone) {
          drvHlpr.scrollIntoView(sr);
          screenshot.get();
          screenshotDone = true;
        }
        if(sr.isDisplayed()) {
          sr.click();
          result = true;
        } else {
          break;
        }
      }
    }
    screenshot.get();
    drvHlpr.driver.findElement(By.id(ToManyField.selectId())).click();
    drvHlpr.wait.until(drv -> drv.findElements(By.id(dialogSlctId)).isEmpty());
    screenshot.get();
    return result;
  }

  public static void login
  (DriverHlpr drvHlpr, String username, String password, Screenshot screenshot)
  throws IOException {
    drvHlpr.driver.get(StdPath.locationUri(drvHlpr.port, StdPath.LOGIN));
    By submitBy = By.xpath("//vaadin-button[@slot='submit']");
    drvHlpr.lwait.until(drv -> drv.findElement(submitBy) != null);
    WebElement usernameCtl = drvHlpr.driver.findElement
    (By.xpath("//input[@name='username']"));
    usernameCtl.sendKeys(username);
    WebElement passwordCtl = drvHlpr.driver.findElement
    (By.xpath("//input[@name='password']"));
    passwordCtl.sendKeys(password);
    if(screenshot != null) {
      screenshot.get();
    }
    drvHlpr.driver.findElement(submitBy).click();
    drvHlpr.wait.until(drv -> !drv.findElements(By.id(MainLayout.logoutId)).isEmpty());
  }

  public static void login(DriverHlpr drvHlpr, String username, String password)
  throws IOException {
    login(drvHlpr, username, password, null);
  }

  public static void logout(DriverHlpr drvHlpr) {
    drvHlpr.driver.findElement(By.id(MainLayout.logoutId)).click();
    drvHlpr.lwait.until(drv -> drv.findElement(By.id(MainLayout.loginId)) != null);
  }

  public static WebElement findNavSection(DriverHlpr drvHlpr, String name) {
    By sectBy = By.xpath("//vaadin-side-nav/vaadin-side-nav-item[text()='" + name + "']");
    SearchContext sectShadowRoot = drvHlpr.driver.findElement(sectBy).getShadowRoot();
    return sectShadowRoot.findElement(By.cssSelector("div:nth-child(1)"));
  }

  public static String gridColumnHeaderXp(String gridId, String column) {
    return "//vaadin-grid[@id='" + gridId + "']/vaadin-grid-cell-content/"
    + "vaadin-grid-sorter[contains(text(),'" + column + "')]";
  }

}
