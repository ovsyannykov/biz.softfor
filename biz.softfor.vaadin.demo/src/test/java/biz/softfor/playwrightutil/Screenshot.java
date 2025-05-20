package biz.softfor.playwrightutil;

import biz.softfor.util.Holder;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Supplier;
import org.apache.commons.io.FileUtils;

public class Screenshot {

  public final static String SCREENSHOT_DIR = "./screenlog/";
  public final static String SCREENSHOT_NAME_TPL = "yyyyMMdd_HHmmss_SSS";

  private final static String EXT = ".png";

  public static Supplier<String> dateTimeMs(String dir) {
    return () -> dir + new SimpleDateFormat(SCREENSHOT_NAME_TPL)
    .format(new Date()) + EXT;
  }

  public static Supplier<String> sequencer(String dir, Holder<Integer> counter) {
    return () -> dir + counter.value++ + EXT;
  }

  private final String dir;
  private final Supplier<String> dirSupplier;
  private final Page.ScreenshotOptions so;
  private final Locator.ScreenshotOptions lo;

  public Screenshot(String dir, Supplier<String> dirSupplier) throws IOException {
    this.dir = dir;
    this.dirSupplier = dirSupplier;
    so = new Page.ScreenshotOptions();
    lo = new Locator.ScreenshotOptions();
    File dirFile = new File(this.dir);
    if(dirFile.exists()) {
      FileUtils.cleanDirectory(dirFile);
    } else {
      dirFile.mkdir();
    }
  }

  public Screenshot(String dir) throws IOException {
    this(dir, null);
  }

  public void get(Page page) {
    page.screenshot(so.setPath(Paths.get(dirSupplier.get())));
  }

  public void get(Page page, String name) {
    page.screenshot(so.setPath(Paths.get(dir + name + EXT)));
  }

  public void get(Page page, String locator, String name) {
    page.locator(locator).screenshot(lo.setPath(Paths.get(dir + name + EXT)));
  }

}
