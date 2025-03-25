package biz.softfor.spring.security.service;

import java.time.Duration;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.list;
import org.junit.jupiter.api.Test;

public class JwtUtilTest {

  @Test
  public void codeDecode() throws Exception {
    JwtUtil jwtUtil = new JwtUtil
    ("krible_KRABLE=bUmS", Duration.ofHours(2L), Duration.ofHours(1L), true);
    TokenDecoded tokenDecoded = jwtUtil.generate
    (314L, 101L, "username", LocalDateTime.now(), list("group1", "group21"));
    TokenDecoded tokenDecoded2 = jwtUtil.decode(tokenDecoded.token);
    assertThat(tokenDecoded.id).isEqualTo(tokenDecoded2.id);
  }

}
