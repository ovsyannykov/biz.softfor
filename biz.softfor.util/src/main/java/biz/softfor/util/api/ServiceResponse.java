package biz.softfor.util.api;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class ServiceResponse {

  public final static String RESPONSE_CODE_IS_NOT_OK = "Response code is not OK.";

  public final int status;
  public final String body;

}
