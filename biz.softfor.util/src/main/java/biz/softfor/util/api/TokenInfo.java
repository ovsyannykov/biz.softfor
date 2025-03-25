package biz.softfor.util.api;

import java.time.LocalDateTime;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class TokenInfo {

  public final String token;
  public final LocalDateTime expired;

}
