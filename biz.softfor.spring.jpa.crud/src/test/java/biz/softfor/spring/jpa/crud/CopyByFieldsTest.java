package biz.softfor.spring.jpa.crud;

import biz.softfor.jpa.crud.querygraph.ColumnDescr;
import biz.softfor.spring.jpa.crud.assets.PartnersTestBasic;
import biz.softfor.user.jpa.User;
import biz.softfor.util.Json;
import biz.softfor.util.StringUtil;
import java.util.List;
import lombok.extern.java.Log;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Log
public class CopyByFieldsTest extends PartnersTestBasic {

  @ParameterizedTest
  @MethodSource(ManyToManyTest.READ)
  public void deepCopy(List<String> fields) throws ReflectiveOperationException {
    User source = data.users.data.get(2);
    log.info(() -> "=".repeat(32)
    + "\nfields=" + (fields == null ? StringUtil.NULL : fields.toString()));
    User copy = ColumnDescr.copyByFields(source, User.class, fields);
    log.info(() -> "sample=" + Json.serializep(om, source));
    log.info(() -> "data=" + Json.serializep(om, copy));
  }

}
