package biz.softfor.playwrightutil;

import biz.softfor.util.Holder;
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

  private final static String EXT = ".png";

  public static Supplier<String> dateTimeMs(String dir) {
    return () -> dir + new SimpleDateFormat("yyyyMMdd_HHmmss_SSS")
    .format(new Date()) + EXT;
  }

  public static Supplier<String> sequencer(String dir, Holder<Integer> counter) {
    return () -> dir + counter.value++ + EXT;
  }

  private final String dir;
  private final Supplier<String> dirSupplier;
  private final Page.ScreenshotOptions so;

  public Screenshot(String dir, Supplier<String> dirSupplier) throws IOException {
    this.dir = dir;
    this.dirSupplier = dirSupplier;
    so = new Page.ScreenshotOptions();
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

}
