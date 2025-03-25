package biz.softfor.user.spring.rest.testassets;

import biz.softfor.util.api.HaveId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Getter
@Setter
public class TestEntityDto extends HaveId<Integer> {

  public final static String EVERYBODY = "everybody";
  public final static String AUTHORIZED = "authorized";
  public final static String NOBODY = "nobody";

  private String everybody;
  private String authorized;
  private String nobody;

}
