package biz.softfor.vaadin.user;

import biz.softfor.address.jpa.State;
import biz.softfor.playwrightutil.DriverHlpr;
import biz.softfor.playwrightutil.Screenshot;
import biz.softfor.playwrightutil.VaadinTestUtil;
import biz.softfor.user.jpa.User;
import biz.softfor.user.jpa.UserGroup;
import biz.softfor.user.jpa.UserGroup_;
import biz.softfor.user.jpa.User_;
import biz.softfor.util.Holder;
import biz.softfor.util.api.StdPath;
import biz.softfor.vaadin.EntityForm;
import biz.softfor.vaadin.EntityView;
import biz.softfor.vaadin.address.StatesView;
import biz.softfor.vaadin.demo.App;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = { App.class })
@UsePlaywright
public class AccessPwTest {

  private final static String ADMIN_USER = "admin";
  private final static String ADMIN_PWD = "admin";

  @LocalServerPort
  private int port;

  @Test
  public void access(TestInfo testInfo, Page page) throws Exception {
    page.setViewportSize(1280, 800);
    DriverHlpr drvHlpr = new DriverHlpr(page, port);
    String screenshotDir = Screenshot.SCREENSHOT_DIR + AccessPwTest.class.getSimpleName()
    + "/" + testInfo.getTestMethod().get().getName() + "/";
    Screenshot screenshot = new Screenshot(screenshotDir, Screenshot.sequencer(screenshotDir, new Holder<>(0)));
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
