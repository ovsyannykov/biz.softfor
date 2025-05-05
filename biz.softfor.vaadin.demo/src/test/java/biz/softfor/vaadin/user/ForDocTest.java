package biz.softfor.vaadin.user;

import biz.softfor.partner.jpa.Partner;
import biz.softfor.partner.jpa.Partner_;
import biz.softfor.playwrightutil.DriverHlpr;
import biz.softfor.playwrightutil.Screenshot;
import biz.softfor.playwrightutil.VaadinTestUtil;
import biz.softfor.user.jpa.Role;
import biz.softfor.user.jpa.User;
import biz.softfor.user.jpa.User_;
import biz.softfor.util.api.StdPath;
import biz.softfor.vaadin.EntityForm;
import biz.softfor.vaadin.EntityView;
import biz.softfor.vaadin.GridColumn;
import biz.softfor.vaadin.MainLayout;
import biz.softfor.vaadin.NotFoundView;
import biz.softfor.vaadin.dbgrid.DbGrid;
import biz.softfor.vaadin.demo.App;
import biz.softfor.vaadin.field.ToManyField;
import biz.softfor.vaadin.partner.PartnersView;
import biz.softfor.vaadin.security.LoginView;
import biz.softfor.vaadin.security.ProfileView;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = { App.class })
@UsePlaywright
public class ForDocTest {

  private final static String SCREENSHOT_DIR = "./screenlog/";
  private final static String ADMIN_ROLE = "ADMIN";
  private final static String ADMIN_USER = "admin";
  private final static String ADMIN_PWD = "admin";

  @LocalServerPort
  private int port;

  @Test
  public void forDoc(TestInfo testInfo, Page page) throws Exception {
    page.setViewportSize(1280, 800);
    DriverHlpr drvHlpr = new DriverHlpr(page, port);
    String screenshotDir = SCREENSHOT_DIR + ForDocTest.class.getSimpleName()
    + "/" + testInfo.getTestMethod().get().getName() + "/";
    Screenshot screenshot = new Screenshot(screenshotDir);
    VaadinTestUtil.login(drvHlpr, ADMIN_USER, ADMIN_PWD, null);

    String name = "NotFoundView";
    page.navigate(StdPath.locationUri(port, NotFoundView.PATH));
    page.locator("//h3[contains(text(),'not found')]").waitFor(DriverHlpr.wait);
    screenshot.get(page, name);

    name = "GridField";
    page.navigate(StdPath.locationUri(port, PartnersView.PATH));
    String entityViewGridId = EntityView.gridId(Partner.class);
    page.locator("#" + entityViewGridId).waitFor(DriverHlpr.wait);
    String[] rows = { "First Co", "John", "Mike", "Plimuth Tax Cons." };
    for(int i = 0; i < rows.length; ++i) {
      page.click("//*[@id='" + entityViewGridId + "']/vaadin-grid-cell-content[text()='" + rows[i] + "']");
      screenshot.get(page, name + i);
    }

    name = "EntityView";
    screenshot.get(page, name + 0);
    page.click("//*[@id='" + entityViewGridId + "']//vaadin-grid-sorter[text()='Registration date/Birthdate']");
    screenshot.get(page, name + 1);
    String filterTypeXp = "//*[@id='" + entityViewGridId + "']/vaadin-grid-cell-content/*[@id='"
    + GridColumn.columnFilterId(Partner_.TYP) + "']/input";
    page.click(filterTypeXp);
    page.click("#vaadin-multi-select-combo-box-item-3");
    page.click("#vaadin-multi-select-combo-box-item-4");
    screenshot.get(page, name + 2);
    page.press("body", "Escape");
    String filtrateId = DbGrid.filtrateId(Partner.class);
    page.click("#" + filtrateId);
    page.locator("//*[@id='" + entityViewGridId + "']/vaadin-grid-cell-content[text()='Person']")
    .waitFor(VaadinTestUtil.DETACHED);
    screenshot.get(page, name + 3);
    page.click("#" + DbGrid.clearId(Partner.class));
    page.click("#" + filtrateId);

    name = "EntityForm";
    String entityFormId = EntityForm.id(Partner.class);
    String entityRowId = "//*[@id='" + entityViewGridId + "']/vaadin-grid-cell-content[text()='Legal entity']";
    page.click(entityRowId);
    page.dblclick(entityRowId);
    page.locator("#" + entityFormId).waitFor(DriverHlpr.wait);
    screenshot.get(page, name);

    name = "LangSelector";
    String langSelectorXp = "//vaadin-app-layout/vaadin-horizontal-layout/vaadin-horizontal-layout/vaadin-combo-box";
    page.locator(langSelectorXp).waitFor(DriverHlpr.wait);
    screenshot.get(page, name + 0);
    page.click(langSelectorXp);
    screenshot.get(page, name + 1);
    page.click("#vaadin-combo-box-item-1");
    page.locator("//span[text()='Language']").waitFor(VaadinTestUtil.DETACHED);
    screenshot.get(page, name + 2);
    page.click(langSelectorXp);
    screenshot.get(page, name + 3);
    page.click("#vaadin-combo-box-item-0");

    name = "OneToManyField";
    page.locator("#" + EntityForm.fieldId(Partner_.CONTACTS)).scrollIntoViewIfNeeded();
    screenshot.get(page, name);

    name = "ManyToOneField";
    String manyToOneField = "//*[@id='" + entityFormId + "']//vaadin-custom-field[label[text()='Location type']]";
    page.locator(manyToOneField).scrollIntoViewIfNeeded();
    screenshot.get(page, name + 0);
    page.click(manyToOneField + "//vaadin-button[1]");
    String locationTypeGridId = ToManyField.gridId(Partner_.LOCATION_TYPE);
    Thread.sleep(100);
    String manyToOneFieldSelectedXp = "//*[@id='" + locationTypeGridId + "']/vaadin-grid-cell-content[text()='branch'][1]";
    page.locator(manyToOneFieldSelectedXp).scrollIntoViewIfNeeded();
    page.click(manyToOneFieldSelectedXp);
    screenshot.get(page, name + 1);
    page.locator("#" + ToManyField.selectId()).click();
    page.locator("#" + locationTypeGridId).waitFor(VaadinTestUtil.DETACHED);
    screenshot.get(page, name + 2);
    page.click("#" + EntityForm.saveId());
    page.locator("#" + entityViewGridId).waitFor(DriverHlpr.wait);
    String filterLocationTypeCss = "#" + GridColumn.columnFilterId(Partner_.LOCATION_TYPE) + " vaadin-button:nth-child(2)";
    page.locator(filterLocationTypeCss).scrollIntoViewIfNeeded();
    screenshot.get(page, name + 3);

    name = "ManyToOneDbGridColumn";
    page.click("#" + DbGrid.clearId(Partner.class));
    page.click("#" + filtrateId);
    page.locator(filterLocationTypeCss).scrollIntoViewIfNeeded();
    screenshot.get(page, name + 0);
    page.click(filterLocationTypeCss);
    page.locator("#" + locationTypeGridId).scrollIntoViewIfNeeded();
    screenshot.get(page, name + 1);
    page.click("//*[@id='" + locationTypeGridId + "']/vaadin-grid-cell-content"
    + "[text()='main office']/preceding-sibling::*[1]/vaadin-checkbox");
    screenshot.get(page, name + 2);
    page.locator("#" + ToManyField.selectId()).click();
    page.locator("#" + locationTypeGridId).waitFor(VaadinTestUtil.DETACHED);
    screenshot.get(page, name + 3);
    page.click("#" + filtrateId);
    page.locator("//*[@id='" + entityViewGridId + "']/vaadin-grid-cell-content[text()='office']")
    .waitFor(VaadinTestUtil.DETACHED);
    screenshot.get(page, name + 4);

    name = "ManyToManyField";
    page.navigate(StdPath.locationUri(port, UsersView.PATH));
    entityViewGridId = EntityView.gridId(User.class);
    page.locator("#" + entityViewGridId).waitFor(DriverHlpr.wait);
    String[] entities = { "admin", "user", "manager" };
    for(int i = 0; i < entities.length; ++i) {
      page.click("//*[@id='" + entityViewGridId + "']/vaadin-grid-cell-content[text()='" + entities[i] + "']");
      screenshot.get(page, name + i);
    }
    entityFormId = EntityForm.id(User.class);
    page.dblclick("//*[@id='" + entityViewGridId + "']/vaadin-grid-cell-content[text()='manager']");
    page.locator("#" + entityFormId).waitFor(DriverHlpr.wait);
    page.locator("#" + EntityForm.fieldId(User_.GROUPS)).scrollIntoViewIfNeeded();
    screenshot.get(page, name + 3);
    String m2mGridId = EntityForm.fieldGridId(User_.GROUPS);
    page.click("#" + ToManyField.addId(User_.GROUPS));
    String dialogSlctId = ToManyField.gridId(User_.GROUPS);
    page.locator("#" + dialogSlctId).waitFor(DriverHlpr.wait);
    Thread.sleep(100);
    screenshot.get(page, name + 4);
    String[] addRows = { "CITIES_EDITORS", "COUNTRIES_EDITORS", "USER" };
    for(String addRow : addRows) {
      page.click("//*[@id='" + dialogSlctId + "']/vaadin-grid-cell-content"
      + "[text()='" + addRow + "']/preceding-sibling::*[1]/vaadin-checkbox");
    }
    screenshot.get(page, name + 5);
    page.locator("#" + ToManyField.selectId()).click();
    page.locator("#" + dialogSlctId).waitFor(VaadinTestUtil.DETACHED);
    screenshot.get(page, name + 6);
    String deletedRowXp = "//*[@id='" + m2mGridId + "']/vaadin-grid-cell-content[text()='USER']";
    page.click(deletedRowXp);
    screenshot.get(page, name + 7);
    page.click("#" + ToManyField.deleteId(User_.GROUPS));
    page.locator(deletedRowXp).waitFor(VaadinTestUtil.HIDDEN);
    screenshot.get(page, name + 8);
    page.click("#" + EntityForm.saveId());
    page.locator("#" + entityFormId).waitFor(VaadinTestUtil.HIDDEN);
    screenshot.get(page, name + 9);

    name = "UserDbGridFilter";
    page.navigate(StdPath.locationUri(port, RolesView.PATH));
    entityViewGridId = EntityView.gridId(Role.class);
    page.locator("#" + entityViewGridId).waitFor(DriverHlpr.wait);
    screenshot.get(page, name + 0);
    dialogSlctId = ToManyField.gridId(User.TITLE);
    page.click("//vaadin-form-layout/vaadin-custom-field/vaadin-horizontal-layout/vaadin-button");
    page.locator("//*[@id='" + dialogSlctId + "']").waitFor(DriverHlpr.wait);
    Thread.sleep(100);
    screenshot.get(page, name + 1);
    page.click("//*[@id='" + dialogSlctId + "']/vaadin-grid-cell-content[text()='" + ADMIN_USER + "']");
    screenshot.get(page, name + 2);
    page.click("#" + ToManyField.selectId());
    page.locator("#" + dialogSlctId).waitFor(VaadinTestUtil.DETACHED);
    screenshot.get(page, name + 3);
    page.click("#" + DbGrid.filtrateId(Role.class));
    page.waitForSelector("//*[@id='" + entityViewGridId + "']/vaadin-grid-cell-content[text()='Users']");
    screenshot.get(page, name + 4);

    name = "ProfileView";
    page.navigate(StdPath.locationUri(port, ProfileView.PATH));
    page.locator("#" + EntityForm.cancelId()).waitFor(DriverHlpr.wait);
    screenshot.get(page, name);

    name = "LoginView";
    VaadinTestUtil.logout(page);
    page.navigate(StdPath.locationUri(port, LoginView.PATH));
    String submitXp = "//vaadin-button[@slot='submit']";
    page.locator(submitXp).waitFor(DriverHlpr.wait);
    screenshot.get(page, name);

    name = "RegistrationView";
    page.click("#" + MainLayout.registrationId);
    page.locator("#" + EntityForm.cancelId()).waitFor(DriverHlpr.wait);
    screenshot.get(page, name);
  }

}
