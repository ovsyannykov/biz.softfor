package biz.softfor.vaadin.user;

import biz.softfor.address.jpa.Postcode;
import biz.softfor.seleniumutil.DriverHlpr;
import biz.softfor.seleniumutil.Screenshot;
import biz.softfor.seleniumutil.VaadinTestUtil;
import biz.softfor.user.jpa.User;
import biz.softfor.user.jpa.UserGroup;
import biz.softfor.user.jpa.UserGroup_;
import biz.softfor.user.jpa.User_;
import biz.softfor.util.Holder;
import biz.softfor.util.api.StdPath;
import biz.softfor.vaadin.EntityForm;
import biz.softfor.vaadin.EntityView;
import biz.softfor.vaadin.address.PostcodesView;
import biz.softfor.vaadin.demo.App;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.util.function.Supplier;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = { App.class })
public class AccessSeTest {

  @LocalServerPort
  private int port;

  private DriverHlpr drvHlpr;

  private final static String DEFAULT_SECTION = "Partners";
  private final static String SCREENSHOT_DIR = "./screenlog/";
  private final static String ADMIN_ROLE = "ADMIN";
  private final static String ADMIN_USER = "admin";
  private final static String ADMIN_PWD = "admin";

  @BeforeAll
  public static void beforeAll() {
    WebDriverManager.chromedriver().setup();
  }

  @BeforeEach
  public void beforeEach() throws Exception {
    drvHlpr = new DriverHlpr(new ChromeDriver(), port);
  }

  @AfterEach
  public void afterEach() {
    drvHlpr.driver.quit();
  }

  @Test
  public void access(TestInfo testInfo) throws Exception {
    String screenshotDir = SCREENSHOT_DIR + AccessSeTest.class.getSimpleName()
    + "/" + testInfo.getTestMethod().get().getName() + "/";
    Supplier<String> seq = Screenshot.sequencer(screenshotDir, new Holder<>(0));
    Screenshot screenshot = new Screenshot(drvHlpr.driver, screenshotDir, seq);
    String sectXpath = "//vaadin-side-nav-item[text()='Address']";
    String EDITORS = "CITIES_EDITORS";
    String checkItem = "Cities";
    By checkItemMenuBy = By.xpath(sectXpath + "/vaadin-side-nav-item[text()='" + checkItem + "']");
    String sectItem = "Postcodes";
    Class<?> sectItemClass = Postcode.class;
    String sectPath = StdPath.locationUri(port) + "/" + PostcodesView.PATH;
    By sectViewBy = By.id(EntityView.gridId(sectItemClass));
    By gridColumnHeaderBy = By.xpath(VaadinTestUtil.gridColumnHeaderXp(EntityView.gridId(sectItemClass), "City"));
    String checkItemDsc = "Address -> " + checkItem + " menu item";
    String checkColumnDsc = sectItem + "." + checkItem + " grid column";
    String groupsViewId = EntityView.gridId(UserGroup.class);
    By groupsViewBy = By.id(groupsViewId);
    By groupFormBy = By.id(EntityForm.id(UserGroup.class));

    //check that the "Address" -> "Cities" menu item not exists for anonymous users
    drvHlpr.driver.get(sectPath);
    drvHlpr.lwait.until(drv -> !drv.findElements(sectViewBy).isEmpty());
    screenshot.get();
    assertThat(drvHlpr.driver.findElements(checkItemMenuBy)).as(checkItemDsc)
    .isEmpty();
    assertThat(drvHlpr.driver.findElements(gridColumnHeaderBy))
    .as(checkColumnDsc).isEmpty();

    //login as "admin" and check that the "Address" -> "Cities" menu item not exists
    VaadinTestUtil.login(drvHlpr, ADMIN_USER, ADMIN_PWD, screenshot);//0
    drvHlpr.driver.get(sectPath);
    drvHlpr.lwait.until(drv -> !drv.findElements(By.xpath(sectXpath)).isEmpty());
    screenshot.get();
    assertThat(drvHlpr.driver.findElements(checkItemMenuBy)).as(checkItemDsc)
    .isEmpty();
    assertThat(drvHlpr.driver.findElements(gridColumnHeaderBy))
    .as(checkColumnDsc).isEmpty();

    //go to User Groups View and add the user "admin" to the EDITORS group
    drvHlpr.driver.get(StdPath.locationUri(port) + "/" + UserGroupsView.PATH);
    drvHlpr.lwait.until(drv -> !drv.findElements(groupsViewBy).isEmpty());
    screenshot.get();
    By groupBy = By.xpath("//*[@id='" + groupsViewId + "']"
    + "/vaadin-grid-cell-content[contains(text(),'" + EDITORS + "')]");
    drvHlpr.driver.findElement(groupBy).click();
    screenshot.get();
    drvHlpr.doubleClick(groupBy);
    drvHlpr.wait.until(drv -> !drv.findElements(groupFormBy).isEmpty());
    boolean added = VaadinTestUtil.addRows2GridField
    (drvHlpr, UserGroup_.USERS, User.class, User_.USERNAME, ADMIN_USER, screenshot);
    assertThat(added).as("User '" + ADMIN_USER + "' added to the group '"
    + EDITORS + "'.").isTrue();
    By rolesGridBy = By.id(EntityForm.fieldId(UserGroup_.ROLES));
    drvHlpr.scrollIntoView(drvHlpr.driver.findElement(rolesGridBy));
    screenshot.get();
    WebElement save = drvHlpr.driver.findElement(By.id(EntityForm.saveId()));
    drvHlpr.scrollIntoView(save);
    screenshot.get();
    save.click();
    drvHlpr.wait.until(drv -> !drv.findElement(groupFormBy).isDisplayed());
    screenshot.get();

    //relogin and check availability of "Cities"
    VaadinTestUtil.logout(drvHlpr);
    screenshot.get();
    VaadinTestUtil.login(drvHlpr, ADMIN_USER, ADMIN_PWD);
    drvHlpr.driver.get(sectPath);
    drvHlpr.lwait.until(drv -> !drv.findElements(sectViewBy).isEmpty());
    screenshot.get();
    assertThat(drvHlpr.driver.findElements(checkItemMenuBy)).as(checkItemDsc)
    .isNotEmpty();
    assertThat(drvHlpr.driver.findElements(gridColumnHeaderBy))
    .as(checkColumnDsc).isNotEmpty();
  }

}
