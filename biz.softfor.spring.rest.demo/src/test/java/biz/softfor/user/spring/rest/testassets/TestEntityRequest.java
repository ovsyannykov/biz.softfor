package biz.softfor.user.spring.rest.testassets;

import biz.softfor.util.api.CreateRequest;
import biz.softfor.util.api.ReadRequest;
import biz.softfor.util.api.StdPath;
import biz.softfor.util.api.UpdateRequest;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;

public class TestEntityRequest {

  public final static String METHOD = "POST";
  public final static String TEST_SECURITY = StdPath.ROOT + "test/security/";
  public final static String DATA_DEFAULT_ACCESS = "dataDefaultAccess";
  public final static String URL_DEFAULT_ACCESS_EVERYBODY = "urlDefaultAccessEverybody";
  public final static String URL_DEFAULT_ACCESS_AUTHORIZED = "urlDefaultAccessAuthorized";
  public final static String URL_DEFAULT_ACCESS_NOBODY = "urlDefaultAccessNobody";
  public final static String DATA_DEFAULT_UPDATE_ACCESS = "dataDefaultUpdateAccess";

  @EqualsAndHashCode(callSuper = true)
  @ToString(callSuper = true)
  public static class Create extends CreateRequest<Integer, TestEntityWor> {

    public Create() {
    }

    public Create(TestEntityWor data) {
      super(data);
    }
  }

  @EqualsAndHashCode(callSuper = true)
  @ToString(callSuper = true)
  public static class Read extends ReadRequest<Integer, TestEntityFltr> {
  }

  @EqualsAndHashCode(callSuper = true)
  @ToString(callSuper = true)
  public static class Update extends UpdateRequest<Integer, TestEntityFltr, TestEntityWor> {

    public Update() {
    }

    public Update(TestEntityWor data) {
      super(data);
    }

    public Update(TestEntityWor data, List<String> fields) {
      super(data, fields);
    }

  }

}
