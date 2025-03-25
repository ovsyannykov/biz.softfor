package biz.softfor.util;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

public class StringUtilTest {

  @Test
  public void camelCaseToUnderScoreUpperCaseTest() {
    String[][] cases = {
      { "DPrev_", "D_PREV_" }
    , { "camel2_Case", "CAMEL2_CASE" }
    , { "_camelCase", "_CAMEL_CASE" }
    , { "camelCase", "CAMEL_CASE" }
    , { "CamelCase", "CAMEL_CASE" }
    , { "camel2Case", "CAMEL2_CASE" }
    };
    for(String[] c : cases) {
      assertThat(StringUtil.camelCaseToUnderScoreUpperCase(c[0])).isEqualTo(c[1]);
    }
  }

}
