package biz.softfor.seleniumutil;

import biz.softfor.util.Holder;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Supplier;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.remote.RemoteWebDriver;

public class Screenshot {

  public final static String SCREENSHOT_DIR = "./screenlog/";

  public static Supplier<String> dateTimeMs(String dir) {
    return () -> dir + new SimpleDateFormat("yyyyMMdd_HHmmss_SSS")
    .format(new Date()) + ".png";
  }

  public static Supplier<String> sequencer(String dir, Holder<Integer> counter) {
    return () -> dir + counter.value++ + ".png";
  }

  private final Supplier<String> dirSupplier;
  private final RemoteWebDriver driver;

  public Screenshot
  (RemoteWebDriver driver, String dir, Supplier<String> dirSupplier)
  throws IOException {
    this.driver = driver;
    this.dirSupplier = dirSupplier;
    File dirFile = new File(dir);
    if(dirFile.exists()) {
      FileUtils.cleanDirectory(dirFile);
    } else {
      dirFile.mkdir();
    }
  }

  public void get() throws IOException {
    File scrFile = driver.getScreenshotAs(OutputType.FILE);
    FileUtils.moveFile(scrFile, new File(dirSupplier.get()));
  }

}
