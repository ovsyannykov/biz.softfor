package biz.softfor.user.spring.rest.testassets;

import biz.softfor.util.Generated;
import biz.softfor.util.api.filter.FilterId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Generated("biz.softfor.spring.rest.security.TestEntity")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Getter
@Setter
public class TestEntityFltr extends FilterId<Integer> {

  public final static String EVERYBODY = "everybody";
  public final static String AUTHORIZED = "authorized";
  public final static String NOBODY = "nobody";

  private String everybody;
  private String authorized;
  private String nobody;

}
