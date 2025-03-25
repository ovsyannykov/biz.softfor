package biz.softfor.vaadin.user;

import biz.softfor.address.jpa.State;
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
import biz.softfor.seleniumutil.DriverHlpr;
import biz.softfor.seleniumutil.Screenshot;
import biz.softfor.seleniumutil.VaadinTestUtil;
import io.github.bonigarcia.wdm.WebDriverManager;
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
    String screenshotDir = SCREENSHOT_DIR + testInfo.getTestMethod().get().getName() + "/";
    Screenshot screenshot = new Screenshot(drvHlpr.driver, screenshotDir, Screenshot.sequencer(screenshotDir, new Holder<>(0)));
    String defaultSect = "Partners";
    String sect = "Address";
    String sectItem = "Cities";
    Class<?> sectItemClass = State.class;
    String sectXpath = "//vaadin-side-nav-item[text()='" + sect + "']";
    By sectMenuBy = By.xpath(sectXpath);
    By itemMenuBy = By.xpath(sectXpath + "/vaadin-side-nav-item[text()='" + sectItem + "']");
    String EDITORS = "CITIES_EDITORS";
    String groupsViewId = EntityView.gridId(UserGroup.class);
    By groupsViewBy = By.id(groupsViewId);
    By groupFormBy = By.id(EntityForm.id(UserGroup.class));
    By saveBy = By.id(EntityForm.saveId());
    By citiesViewBy = By.id(EntityView.gridId(sectItemClass));

    //login as "admin" and check that the "Address" -> "Cities" menu item not exists
    VaadinTestUtil.login(drvHlpr, ADMIN_USER, ADMIN_PWD, screenshot);//0
    drvHlpr.lwait.until(drv -> !drv.findElements(sectMenuBy).isEmpty());
    VaadinTestUtil.sideNavSectionClick(drvHlpr, defaultSect);
    VaadinTestUtil.sideNavSectionClick(drvHlpr, sect);
    screenshot.get();//1
    assertThat(drvHlpr.driver.findElements(itemMenuBy)).as("Address -> " + sectItem + " menu item").isEmpty();

    //go to User Groups View and add the user "admin" to the group "CITIES_EDITORS"
    drvHlpr.driver.get(StdPath.locationUri(port) + "/" + UserGroupsView.PATH);
    drvHlpr.lwait.until(drv -> !drv.findElements(groupsViewBy).isEmpty());
    screenshot.get();//2
    By groupBy = By.xpath("//*[@id='" + groupsViewId + "']"
    + "/vaadin-grid-cell-content[contains(text(),'" + EDITORS + "')]");
    drvHlpr.driver.findElement(groupBy).click();
    screenshot.get();//3
    drvHlpr.doubleClick(groupBy);
    drvHlpr.wait.until(drv -> !drv.findElements(groupFormBy).isEmpty());
    boolean added = VaadinTestUtil.addRows2GridField
    (drvHlpr, UserGroup_.USERS, User.class, User_.USERNAME, ADMIN_USER, screenshot);
    assertThat(added).as("User '" + ADMIN_USER + "' added to the group '" + EDITORS + "'.").isTrue();
    WebElement rolesGrid = drvHlpr.driver.findElement(By.id(EntityForm.fieldId(UserGroup_.ROLES)));
    drvHlpr.scrollIntoView(rolesGrid);
    screenshot.get();
    WebElement save = drvHlpr.driver.findElement(saveBy);
    drvHlpr.scrollIntoView(save);
    screenshot.get();
    save.click();
    drvHlpr.wait.until(drv -> !drv.findElement(groupFormBy).isDisplayed());
    screenshot.get();

    //relogin and check availability of "States"
    VaadinTestUtil.logout(drvHlpr);
    screenshot.get();
    VaadinTestUtil.login(drvHlpr, ADMIN_USER, ADMIN_PWD);
    assertThat(drvHlpr.driver.findElements(itemMenuBy)).as("Address -> Cities menu item").isNotEmpty();
    drvHlpr.driver.get(StdPath.locationUri(port) + "/" + StatesView.PATH);
    drvHlpr.lwait.until(drv -> !drv.findElements(citiesViewBy).isEmpty());
    screenshot.get();
  }

}
