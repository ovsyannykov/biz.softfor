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

  public static Supplier<String> dateTimeMs(String dir) {
    return () -> dir + new SimpleDateFormat("yyyyMMdd_HHmmss_SSS")
    .format(new Date()) + ".png";
  }

  public static Supplier<String> sequencer(String dir, Holder<Integer> counter) {
    return () -> dir + counter.value++ + ".png";
  }

  private final Supplier<String> dirSupplier;
  private final Page.ScreenshotOptions so;

  public Screenshot(String dir, Supplier<String> dirSupplier) throws IOException {
    this.dirSupplier = dirSupplier;
    so = new Page.ScreenshotOptions();
    File dirFile = new File(dir);
    if(dirFile.exists()) {
      FileUtils.cleanDirectory(dirFile);
    } else {
      dirFile.mkdir();
    }
  }

  public void get(Page page) {
    page.screenshot(so.setPath(Paths.get(dirSupplier.get())));
  }

}
