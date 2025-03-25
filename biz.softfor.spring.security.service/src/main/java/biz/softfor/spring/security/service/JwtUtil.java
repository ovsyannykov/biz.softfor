package biz.softfor.spring.security.service;

import biz.softfor.user.jpa.TokenWor;
import biz.softfor.util.DateUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.AeadAlgorithm;
import io.jsonwebtoken.security.KeyAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.Password;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

  private final Duration lifetime;
  private final Duration refreshLifetime;
  private final boolean encrypt;
  private final SecretKey secret;
  private final JwtParser parser;

  private final static AeadAlgorithm ALGORITHM = Jwts.ENC.A256GCM;
  private final static KeyAlgorithm<Password, Password> KEY_ALGORITHM
  = Jwts.KEY.PBES2_HS512_A256KW;

  /**@see https://github.com/jwtk/jjwt#jwt-encrypted-directly-with-a-secretkey*/
  public JwtUtil(
    @Value("${biz.softfor.spring.security.jwt.secret}") String secret
  , @Value("${biz.softfor.spring.security.jwt.lifetime}") Duration lifetime
  , @Value("${biz.softfor.spring.security.jwt.refreshLifetime}") Duration refreshLifetime
  , @Value("${biz.softfor.spring.security.jwt.encrypt:true}") boolean encrypt
  ) {
    this.lifetime = lifetime;
    this.refreshLifetime = refreshLifetime;
    this.encrypt = encrypt;
    if(encrypt) {
      this.secret = Keys.password(secret.toCharArray());
      parser = Jwts.parser().decryptWith(this.secret).build();
    } else {
      this.secret = Keys.hmacShaKeyFor
      (Encoders.BASE64.encode(secret.getBytes()).getBytes());
      parser = Jwts.parser().verifyWith(this.secret).build();
    }
  }

  public TokenDecoded decode(String token) {
    TokenDecoded result;
    try {
      Claims claims;
      if(encrypt) {
        claims = parser.parseEncryptedClaims(token).getPayload();
      } else {
        claims = parser.parseSignedClaims(token).getPayload();
      }
      String idClaim = claims.getId();
      Long id = idClaim == null ? null : Long.valueOf(idClaim);
      Long userId = claims.get(TokenWor.USER_ID, Long.class);
      String username = claims.getSubject();
      LocalDateTime expired = DateUtil.toLocalDateTime(claims.getExpiration());
      boolean isRefresh = claims.get(TokenWor.IS_REFRESH, Boolean.class);
      List<String> groups = claims.get(TokenWor.GROUPS, List.class);
      result = new TokenDecoded
      (id, userId, username, expired, isRefresh, groups, token);
    }
    catch(JwtException | IllegalArgumentException ex) {
      result = null;
    }
    return result;
  }

  public LocalDateTime expiredAccess(LocalDateTime issued) {
    return issued.plusSeconds(lifetime.toSeconds());
  }

  public LocalDateTime expiredRefresh(LocalDateTime issued) {
    return issued.plusSeconds(refreshLifetime.toSeconds());
  }

  public TokenDecoded generate(
    Long id
  , Long userId
  , String username
  , LocalDateTime issued
  , Collection<String> groups
  ) {
    return generate
    (id, userId, username, Boolean.FALSE, groups, issued, lifetime);
  }

  public TokenDecoded generateRefresh
  (Long id, Long userId, String username, LocalDateTime issued) {
    return generate(
      id
    , userId
    , username
    , Boolean.TRUE
    , Collections.EMPTY_LIST
    , issued
    , refreshLifetime
    );
  }

  private LocalDateTime expired(LocalDateTime issued, Duration lifetime) {
    return issued.plusSeconds(lifetime.toSeconds());
  }

  private TokenDecoded generate(
    Long id
  , Long userId
  , String username
  , Boolean isRefresh
  , Collection<String> groups
  , LocalDateTime issued
  , Duration lifetime
  ) {
    Map<String, Object> claims = new HashMap<>();
    claims.put(TokenWor.USER_ID, userId);
    claims.put(TokenWor.IS_REFRESH, isRefresh);
    claims.put(TokenWor.GROUPS, groups);
    LocalDateTime expired = expired(issued, lifetime);
    JwtBuilder builder = Jwts.builder()
    .claims(claims)
    .id(Objects.toString(id))
    .subject(username)
    .issuedAt(DateUtil.toDate(issued))
    .expiration(DateUtil.toDate(expired));
    JwtBuilder tokenBldr;
    if(encrypt) {
      tokenBldr = builder.encryptWith((Password)secret, KEY_ALGORITHM, ALGORITHM);
    } else {
      tokenBldr = builder.signWith(secret, Jwts.SIG.HS256);
    }
    String token = tokenBldr.compact();
    return new TokenDecoded
    (id, userId, username, expired, isRefresh, groups, token);
  }

}
