package biz.softfor.spring.security.service;

import biz.softfor.util.api.TokenInfo;
import java.time.LocalDateTime;
import java.util.Collection;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class TokenDecoded {

  public final Long id;
  public final Long userId;
  public final String username;
  public final LocalDateTime expired;
  public final boolean isRefresh;
  public final Collection<String> groups;
  public final String token;

  public TokenInfo info() {
    return new TokenInfo(token, expired);
  }

}
