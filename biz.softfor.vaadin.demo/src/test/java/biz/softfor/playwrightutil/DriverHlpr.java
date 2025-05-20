package biz.softfor.playwrightutil;

import com.microsoft.playwright.Locator.WaitForOptions;
import com.microsoft.playwright.Page;

public class DriverHlpr {

  public final static WaitForOptions wait = new WaitForOptions().setTimeout(10000);
  public final static WaitForOptions lwait = new WaitForOptions().setTimeout(50000);

  public final Page page;
  public final int port;

  public DriverHlpr(Page page, int port) {
    this.page = page;
    this.port = port;
  }

}
