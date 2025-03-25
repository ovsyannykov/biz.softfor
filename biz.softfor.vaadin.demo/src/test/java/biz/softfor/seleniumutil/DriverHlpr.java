package biz.softfor.seleniumutil;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DriverHlpr {

  public final static Duration WAIT = Duration.ofSeconds(10);
  public final static Duration LWAIT = Duration.ofSeconds(50);
  public final static Duration POLLING_WAIT = Duration.ofMillis(500);
  public final static Duration JS_WAIT = Duration.ofMillis(120);
  public final static String SCREENSHOT_NAME_TPL = "yyyyMMdd_HHmmss_SSS";

  public final RemoteWebDriver driver;
  public final Actions act;
  public final WebDriverWait wait;
  public final WebDriverWait lwait;

  public final int port;

  public DriverHlpr(RemoteWebDriver driver, int port) {
    this.driver = driver;
    act = new Actions(this.driver);
    wait = new WebDriverWait(this.driver, WAIT, POLLING_WAIT);
    lwait = new WebDriverWait(this.driver, LWAIT, POLLING_WAIT);
    this.port = port;
  }

  public void doubleClick(By elemBy) {
    act.doubleClick(driver.findElement(elemBy)).perform();
  }

  public void scrollIntoView(WebElement we) {
    driver.executeScript("arguments[0].scrollIntoView(true);", we);
    act.pause(JS_WAIT);
  }

}
