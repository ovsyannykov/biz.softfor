package biz.softfor.vaadin.user;

import biz.softfor.address.jpa.State;
import biz.softfor.partner.jpa.Partner;
import biz.softfor.partner.jpa.Partner_;
import biz.softfor.playwrightutil.DriverHlpr;
import biz.softfor.playwrightutil.Screenshot;
import biz.softfor.playwrightutil.VaadinTestUtil;
import biz.softfor.user.jpa.User;
import biz.softfor.user.jpa.UserGroup;
import biz.softfor.user.jpa.UserGroup_;
import biz.softfor.user.jpa.User_;
import biz.softfor.util.api.StdPath;
import biz.softfor.vaadin.EntityForm;
import biz.softfor.vaadin.EntityView;
import biz.softfor.vaadin.GridColumn;
import biz.softfor.vaadin.NotFoundView;
import biz.softfor.vaadin.address.StatesView;
import biz.softfor.vaadin.dbgrid.DbGrid;
import biz.softfor.vaadin.demo.App;
import biz.softfor.vaadin.partner.PartnersView;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import com.microsoft.playwright.options.WaitForSelectorState;
import static org.assertj.core.api.Assertions.assertThat;
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
    page.setViewportSize(1280, 720);
    DriverHlpr drvHlpr = new DriverHlpr(page, port);
    String screenshotDir = SCREENSHOT_DIR + ForDocTest.class.getSimpleName()
    + "/" + testInfo.getTestMethod().get().getName() + "/";
    Screenshot screenshot = new Screenshot(screenshotDir);
    VaadinTestUtil.login(drvHlpr, ADMIN_USER, ADMIN_PWD, null);

    String name = "NotFoundView";
if(true) {
    page.navigate(StdPath.locationUri(port, NotFoundView.PATH));
    String langSelectorXp = "//vaadin-app-layout/vaadin-horizontal-layout/vaadin-horizontal-layout/vaadin-combo-box";
    page.locator(langSelectorXp).waitFor(DriverHlpr.lwait);
    screenshot.get(page, name + 0);
    page.click(langSelectorXp);
    page.click("#vaadin-combo-box-item-1");
    screenshot.get(page, name + 1);
    page.click(langSelectorXp);
    page.click("#vaadin-combo-box-item-0");
  }
  
    name = "EntityView";
    page.navigate(StdPath.locationUri(port, PartnersView.PATH));
    String entityViewGridId = EntityView.gridId(Partner.class);
    page.locator("#" + entityViewGridId).waitFor(DriverHlpr.lwait);
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
    page.click("#" + DbGrid.filtrateId(Partner.class));
    page.locator("//*[@id='" + entityViewGridId + "']/vaadin-grid-cell-content[text()='Person']")
    .waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.DETACHED));
    screenshot.get(page, name + 3);

    name = "EntityForm";
    page.dblclick("//*[@id='" + entityViewGridId + "']/vaadin-grid-cell-content[text()='Legal entity']");
    page.locator("#" + EntityForm.id(Partner.class)).waitFor(DriverHlpr.wait);
    screenshot.get(page, name);
    
  if(false) {
    String sectXpath = "//vaadin-side-nav-item[text()='Address']";
    String EDITORS = "COUNTRIES_EDITORS";
    String checkItem = "Countries";
    String checkItemXpath = sectXpath + "/vaadin-side-nav-item[text()='" + checkItem + "']";
    String sectItem = "States";
    Class<?> sectItemClass = State.class;
    String sectPath = StdPath.locationUri(port, StatesView.PATH);
    String sectViewId = EntityView.gridId(sectItemClass);
    String gridColumnHeaderXpath = VaadinTestUtil.gridColumnHeaderXp(EntityView.gridId(sectItemClass), "Country");
    String checkItemDsc = "Address -> " + checkItem + " menu item";
    String checkColumnDsc = sectItem + "." + checkItem + " grid column";
    String groupsViewId = EntityView.gridId(UserGroup.class);
    String groupFormId = EntityForm.id(UserGroup.class);

    //check that the "Address" -> "Countries" menu item not exists for anonymous users
    page.navigate(sectPath);
    page.locator("#" + sectViewId).waitFor(DriverHlpr.lwait);
    screenshot.get(page);
    assertThat(page.locator(checkItemXpath).all()).as(checkItemDsc).isEmpty();
    assertThat(page.locator(gridColumnHeaderXpath).all()).as(checkColumnDsc).isEmpty();

    //login as "admin" and check that the "Address" -> "Countries" menu item not exists
    VaadinTestUtil.login(drvHlpr, ADMIN_USER, ADMIN_PWD, screenshot);
    page.navigate(sectPath);
    page.locator("#" + sectViewId).waitFor(DriverHlpr.lwait);
    screenshot.get(page);
    assertThat(page.locator(checkItemXpath).all()).as(checkItemDsc).isEmpty();
    assertThat(page.locator(gridColumnHeaderXpath).all()).as(checkColumnDsc).isEmpty();

    //go to User Groups View and add the user "admin" to the EDITORS group
    page.navigate(StdPath.locationUri(port, UserGroupsView.PATH));
    page.locator("#" + groupsViewId).waitFor(DriverHlpr.lwait);
    screenshot.get(page);
    Locator groupBy = page.locator("//*[@id='" + groupsViewId + "']"
    + "/vaadin-grid-cell-content[text()='" + EDITORS + "']");
    groupBy.click();
    screenshot.get(page);
    groupBy.dblclick();
    page.locator("#" + groupFormId).waitFor(DriverHlpr.wait);
    boolean added = VaadinTestUtil.addRows2GridField
    (page, UserGroup_.USERS, User.class, User_.USERNAME, ADMIN_USER, screenshot);
    assertThat(added).as("User '" + ADMIN_USER + "' added to the group '"
    + EDITORS + "'.").isTrue();
    page.locator("#" + EntityForm.fieldId(UserGroup_.ROLES))
    .scrollIntoViewIfNeeded();
    screenshot.get(page);
    Locator save = page.locator("#" + EntityForm.saveId());
    save.scrollIntoViewIfNeeded();
    screenshot.get(page);
    save.click();
    page.locator("#" + groupFormId).waitFor();
    screenshot.get(page);

    //relogin and check availability of "Countries"
    VaadinTestUtil.logout(page);
    screenshot.get(page);
    VaadinTestUtil.login(drvHlpr, ADMIN_USER, ADMIN_PWD);
    page.navigate(sectPath);
    page.locator(sectXpath).waitFor(DriverHlpr.lwait);
    screenshot.get(page);
    assertThat(page.locator(checkItemXpath).all()).as(checkItemDsc).isNotEmpty();
    assertThat(page.locator(gridColumnHeaderXpath).all()).as(checkColumnDsc).isNotEmpty();
  }
  }

}
