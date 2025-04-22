package biz.softfor.user.spring.rest.testassets;

import biz.softfor.util.api.CreateRequest;
import biz.softfor.util.api.DeleteRequest;
import biz.softfor.util.api.ReadRequest;
import biz.softfor.util.api.StdPath;
import biz.softfor.util.api.UpdateRequest;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;

public class TeztEntityRequest {

  public final static String METHOD = "POST";
  public final static String TEST_SECURITY = StdPath.ROOT + "test/security/";
  public final static String DATA_DEFAULT_ACCESS = "dataDefaultAccess";
  public final static String URL_DEFAULT_ACCESS_EVERYBODY = "urlDefaultAccessEverybody";
  public final static String URL_DEFAULT_ACCESS_AUTHORIZED = "urlDefaultAccessAuthorized";
  public final static String URL_DEFAULT_ACCESS_NOBODY = "urlDefaultAccessNobody";
  public final static String DATA_DEFAULT_UPDATE_ACCESS = "dataDefaultUpdateAccess";

  @EqualsAndHashCode(callSuper = true)
  @ToString(callSuper = true)
  public static class Create extends CreateRequest<Integer, TeztEntityWor> {

    public Create() {
    }

    public Create(TeztEntityWor data) {
      super(data);
    }
  }

  @EqualsAndHashCode(callSuper = true)
  @ToString(callSuper = true)
  public static class Read extends ReadRequest<Integer, TeztEntityFltr> {
  }

  @EqualsAndHashCode(callSuper = true)
  @ToString(callSuper = true)
  public static class Update extends UpdateRequest<Integer, TeztEntityFltr, TeztEntityWor> {

    public Update() {
    }

    public Update(TeztEntityWor data) {
      super(data);
    }

    public Update(TeztEntityWor data, List<String> fields) {
      super(data, fields);
    }

  }

  @ToString(
      callSuper = true
  )
  @EqualsAndHashCode(
      callSuper = true
  )
  public static class Delete extends DeleteRequest<Integer, TeztEntityFltr> {
  }

}
