package biz.softfor.logback;

import lombok.extern.java.Log;
import org.junit.jupiter.api.Test;

@Log
public class LogTest {

  @Test
  void logLevels() throws Exception {
    log.severe("logTest severe");//TRACE, DEBUG, INFO, WARN, ERROR
    log.warning("logTest warning");//TRACE, DEBUG, INFO, WARN
    log.info("logTest info");//TRACE, DEBUG, INFO
    log.config("logTest config");//TRACE, DEBUG
    log.fine("logTest fine");//TRACE, DEBUG
    log.finer("logTest finer");//TRACE
    log.finest("logTest finest");//TRACE
  }

}
