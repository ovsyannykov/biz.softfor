package biz.softfor.vaadin;

import biz.softfor.partner.jpa.Partner;
import biz.softfor.partner.jpa.Partner_;
import biz.softfor.playwrightutil.DriverHlpr;
import biz.softfor.playwrightutil.Screenshot;
import biz.softfor.playwrightutil.VaadinTestUtil;
import biz.softfor.user.jpa.Role;
import biz.softfor.user.jpa.User;
import biz.softfor.user.jpa.User_;
import biz.softfor.util.api.StdPath;
import biz.softfor.vaadin.dbgrid.DbGrid;
import biz.softfor.vaadin.demo.App;
import biz.softfor.vaadin.field.ToManyField;
import biz.softfor.vaadin.partner.PartnersView;
import biz.softfor.vaadin.security.LoginView;
import biz.softfor.vaadin.security.ProfileView;
import biz.softfor.vaadin.user.RolesView;
import biz.softfor.vaadin.user.UsersView;
import com.microsoft.playwright.Locator;
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
    Locator gridFieldLocator = page.locator
    ("//vaadin-vertical-layout/vaadin-vertical-layout[.//span[text()='Contacts']]");
    Object gridFieldStyle = null;
    String entityViewGridId = EntityView.gridId(Partner.class);
    page.locator("#" + entityViewGridId).waitFor(DriverHlpr.wait);
    screenshot.get(page, name + 0);
    String[] rows = { "First Co", "John", "Mike", "Plimuth Tax Cons." };
    for(int i = 0; i < rows.length; ++i) {
      if(i == 0) {
        gridFieldStyle = VaadinTestUtil.highlight(gridFieldLocator);
      }
      page.click("//*[@id='" + entityViewGridId + "']/vaadin-grid-cell-content[text()='" + rows[i] + "']");
      screenshot.get(page, name + (i + 1));
    }
    VaadinTestUtil.unhighlight(gridFieldLocator, gridFieldStyle);

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
    page.dblclick("//*[@id='" + entityViewGridId + "']/vaadin-grid-cell-content[text()='Legal entity']");
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
    Locator manyToOneFieldLocator = page.locator(manyToOneField);
    manyToOneFieldLocator.scrollIntoViewIfNeeded();
    screenshot.get(page, name + 0);
    Object manyToOneFieldStyle = VaadinTestUtil.highlight(manyToOneFieldLocator);
    screenshot.get(page, name + 1);
    page.click(manyToOneField + "/vaadin-text-field/vaadin-button[2]");
    String locationTypeGridId = ToManyField.gridId(Partner_.LOCATION_TYPE);
    String manyToOneFieldSelectedXp = "//*[@id='" + locationTypeGridId + "']/vaadin-grid-cell-content[text()='branch'][1]";
    page.locator(manyToOneFieldSelectedXp).scrollIntoViewIfNeeded();
    page.click(manyToOneFieldSelectedXp);
    screenshot.get(page, name + 2);
    page.locator("#" + ToManyField.selectId()).click();
    page.locator("#" + locationTypeGridId).waitFor(VaadinTestUtil.DETACHED);
    screenshot.get(page, name + 3);
    VaadinTestUtil.unhighlight(manyToOneFieldLocator, manyToOneFieldStyle);
    page.click("#" + EntityForm.saveId());
    page.locator("#" + entityViewGridId).waitFor(DriverHlpr.wait);
    Locator manyToOneCell = page.locator("//*[@id='" + entityViewGridId + "']/vaadin-grid-cell-content[text()='branch']");
    manyToOneCell.scrollIntoViewIfNeeded();
    Object manyToOneCellStyle = VaadinTestUtil.highlight(manyToOneCell);
    screenshot.get(page, name + 4);
    VaadinTestUtil.unhighlight(manyToOneCell, manyToOneCellStyle);

    name = "ManyToOneDbGridColumn";
    Locator m2oDbGridColumn = page.locator("#" + GridColumn.columnFilterId(Partner_.LOCATION_TYPE));
    page.click("#" + DbGrid.clearId(Partner.class));
    page.click("#" + filtrateId);
    m2oDbGridColumn.scrollIntoViewIfNeeded();
    screenshot.get(page, name + 0);
    Object m2oDbGridColumnStyle = VaadinTestUtil.highlight(m2oDbGridColumn);
    screenshot.get(page, name + 1);
    m2oDbGridColumn.locator("//vaadin-button[2]").click();
    page.locator("#" + locationTypeGridId).scrollIntoViewIfNeeded();
    screenshot.get(page, name + 2);
    page.click("//*[@id='" + locationTypeGridId + "']/vaadin-grid-cell-content"
    + "[text()='main office']/preceding-sibling::*[1]/vaadin-checkbox");
    screenshot.get(page, name + 3);
    page.locator("#" + ToManyField.selectId()).click();
    page.locator("#" + locationTypeGridId).waitFor(VaadinTestUtil.DETACHED);
    screenshot.get(page, name + 4);
    page.click("#" + filtrateId);
    page.locator("//*[@id='" + entityViewGridId + "']/vaadin-grid-cell-content[text()='office']")
    .waitFor(VaadinTestUtil.DETACHED);
    screenshot.get(page, name + 5);
    VaadinTestUtil.unhighlight(m2oDbGridColumn, m2oDbGridColumnStyle);

    name = "ManyToManyField";
    Locator m2mFieldRo
    = page.locator("//*[@id='" + EntityView.id(User.class) + "']/vaadin-split-layout/vaadin-vertical-layout/vaadin-vertical-layout[.//span[text()='Groups']]");
    Locator m2mField = page.locator("#" + EntityForm.fieldId(User_.GROUPS));
    page.navigate(StdPath.locationUri(port, UsersView.PATH));
    entityViewGridId = EntityView.gridId(User.class);
    page.locator("#" + entityViewGridId).waitFor(DriverHlpr.wait);
    screenshot.get(page, name + 0);
    Object m2mFieldRoStyle = VaadinTestUtil.highlight(m2mFieldRo);
    screenshot.get(page, name + 1);
    String[] entities = { "admin", "user", "manager" };
    for(int i = 0; i < entities.length; ++i) {
      page.click("//*[@id='" + entityViewGridId + "']/vaadin-grid-cell-content[text()='" + entities[i] + "']");
      screenshot.get(page, name + (i + 2));
    }
    entityFormId = EntityForm.id(User.class);
    page.dblclick("//*[@id='" + entityViewGridId + "']/vaadin-grid-cell-content[text()='manager']");
    page.locator("#" + entityFormId).waitFor(DriverHlpr.wait);
    m2mField.scrollIntoViewIfNeeded();
    screenshot.get(page, name + 5);
    Object m2mFieldStyle = VaadinTestUtil.highlight(m2mField);
    screenshot.get(page, name + 6);
    String m2mGridId = EntityForm.fieldGridId(User_.GROUPS);
    page.click("#" + ToManyField.addId(User_.GROUPS));
    String dialogSlctId = ToManyField.gridId(User_.GROUPS);
    page.locator("#" + dialogSlctId).scrollIntoViewIfNeeded();
    screenshot.get(page, name + 7);
    String[] addRows = { "CITIES_EDITORS", "COUNTRIES_EDITORS", "USER" };
    for(String addRow : addRows) {
      page.click("//*[@id='" + dialogSlctId + "']/vaadin-grid-cell-content"
      + "[text()='" + addRow + "']/preceding-sibling::*[1]/vaadin-checkbox");
    }
    screenshot.get(page, name + 8);
    page.click("#" + ToManyField.selectId());
    page.locator("#" + dialogSlctId).waitFor(VaadinTestUtil.DETACHED);
    screenshot.get(page, name + 9);
    String deletedRowXp = "//*[@id='" + m2mGridId + "']/vaadin-grid-cell-content[text()='USER']";
    page.click(deletedRowXp);
    screenshot.get(page, name + 10);
    page.click("#" + ToManyField.deleteId(User_.GROUPS));
    page.locator(deletedRowXp).waitFor(VaadinTestUtil.HIDDEN);
    screenshot.get(page, name + 11);
    VaadinTestUtil.unhighlight(m2mField, m2mFieldStyle);
    page.click("#" + EntityForm.saveId());
    page.locator("#" + entityFormId).waitFor(VaadinTestUtil.HIDDEN);
    screenshot.get(page, name + 12);
    VaadinTestUtil.unhighlight(m2mFieldRo, m2mFieldRoStyle);

    name = "UserDbGridFilter";
    page.navigate(StdPath.locationUri(port, RolesView.PATH));
    entityViewGridId = EntityView.gridId(Role.class);
    page.locator("#" + entityViewGridId).waitFor(DriverHlpr.wait);
    screenshot.get(page, name + 0);
    Locator gridFilter = page.locator("//vaadin-custom-field[label[text()='User']]");
    Object gridFilterStyle = VaadinTestUtil.highlight(gridFilter);
    screenshot.get(page, name + 1);
    dialogSlctId = ToManyField.gridId(User.TITLE);
    gridFilter.locator("xpath=/vaadin-text-field/vaadin-button[2]").click();
    page.locator("//*[@id='" + dialogSlctId + "']").scrollIntoViewIfNeeded();
    screenshot.get(page, name + 2);
    page.click("//*[@id='" + dialogSlctId + "']/vaadin-grid-cell-content[text()='" + ADMIN_USER + "']");
    screenshot.get(page, name + 3);
    page.click("#" + ToManyField.selectId());
    page.locator("#" + dialogSlctId).waitFor(VaadinTestUtil.DETACHED);
    screenshot.get(page, name + 4);
    page.click("#" + DbGrid.filtrateId(Role.class));
    page.waitForSelector("//*[@id='" + entityViewGridId + "']/vaadin-grid-cell-content[text()='Users']");
    screenshot.get(page, name + 5);
    page.click("#" + DbGrid.clearId(Role.class));
    VaadinTestUtil.unhighlight(gridFilter, gridFilterStyle);

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
