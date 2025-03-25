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

  private final static String SCREENSHOT_DIR = "./screenlog/";
  private final static String ADMIN_ROLE = "ADMIN";
  private final static String ADMIN_USER = "admin";
  private final static String ADMIN_PWD = "admin";

  @LocalServerPort
  private int port;

  @Test
  public void access(TestInfo testInfo, Page page) throws Exception {
    DriverHlpr drvHlpr = new DriverHlpr(page, port);
    String screenshotDir = SCREENSHOT_DIR + testInfo.getTestMethod().get().getName() + "/";
    Screenshot screenshot = new Screenshot(screenshotDir, Screenshot.sequencer(screenshotDir, new Holder<>(0)));
    String defaultSect = "Partners";
    String sect = "Address";
    String sectItem = "States";
    String sectXpath = "//vaadin-side-nav-item[text()='" + sect + "']";
    String itemMenuBy = sectXpath + "/vaadin-side-nav-item[text()='" + sectItem + "']";
    String EDITORS = "STATES_EDITORS";
    String groupsViewId = EntityView.gridId(UserGroup.class);
    String groupForm = EntityForm.id(UserGroup.class);
    String statesView = EntityView.gridId(State.class);

    //login as "admin" and check that the "Address" -> "States" menu item not exists
    VaadinTestUtil.login(drvHlpr, ADMIN_USER, ADMIN_PWD, screenshot);//0
    page.locator(sectXpath).waitFor(DriverHlpr.lwait);
    VaadinTestUtil.sideNavSectionClick(page, defaultSect);
    VaadinTestUtil.sideNavSectionClick(page, sect);
    screenshot.get(page);//1
    assertThat(page.locator(itemMenuBy).all()).as("Address -> States menu item").isEmpty();

    //go to User Groups View and add the user "admin" to the group "STATES_EDITORS"
    page.navigate(StdPath.locationUri(port) + "/" + UserGroupsView.PATH);
    page.locator("#" + groupsViewId).waitFor(DriverHlpr.lwait);
    screenshot.get(page);//2
    Locator groupBy = page.locator("//*[@id='" + groupsViewId + "']"
    + "/vaadin-grid-cell-content[text()='" + EDITORS + "']");
    groupBy.click();
    screenshot.get(page);//3
    groupBy.dblclick();
    page.locator("#" + groupForm).waitFor(DriverHlpr.wait);
    boolean added = VaadinTestUtil.addRows2GridField
    (page, UserGroup_.USERS, User.class, User_.USERNAME, ADMIN_USER, screenshot);
    assertThat(added).as("User '" + ADMIN_USER + "' added to the group '" + EDITORS + "'.").isTrue();
    page.locator("#" + EntityForm.fieldId(UserGroup_.ROLES)).scrollIntoViewIfNeeded();
    screenshot.get(page);
    Locator save = page.locator("#" + EntityForm.saveId());
    save.scrollIntoViewIfNeeded();
    screenshot.get(page);
    save.click();
    page.locator("#" + groupForm).waitFor();
    screenshot.get(page);

    //relogin and check availability of "States"
    VaadinTestUtil.logout(page);
    screenshot.get(page);
    VaadinTestUtil.login(drvHlpr, ADMIN_USER, ADMIN_PWD);
    assertThat(page.locator(itemMenuBy).all()).as("Address -> States menu item").isNotEmpty();
    page.navigate(StdPath.locationUri(port) + "/" + StatesView.PATH);
    page.locator("#" + statesView).waitFor(DriverHlpr.lwait);
    screenshot.get(page);
  }

}
