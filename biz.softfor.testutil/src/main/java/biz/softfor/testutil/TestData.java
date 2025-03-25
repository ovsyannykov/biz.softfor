package biz.softfor.testutil;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class TestData {

  protected ObjectMapper om;

  public TestData(ObjectMapper om) {
    this.om = om;
  }

  abstract public void detach();
  abstract public void log();
  abstract public void persist();
  abstract public void read();
  abstract public void remove();

  public final void init() {
    persist();
    read();
    detach();
    log();
  }

}
