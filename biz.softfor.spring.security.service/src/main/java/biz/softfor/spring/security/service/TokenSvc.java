package biz.softfor.spring.security.service;

import biz.softfor.spring.jpa.crud.CrudSvc;
import biz.softfor.user.api.TokenFltr;
import biz.softfor.user.jpa.Token;
import biz.softfor.user.jpa.TokenWor;
import biz.softfor.util.RequestUtil;
import biz.softfor.util.api.AbstractRequest;
import biz.softfor.util.api.TokenInfo;
import biz.softfor.util.security.IgnoreAccess;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@IgnoreAccess
@RequiredArgsConstructor
public class TokenSvc extends CrudSvc<Long, Token, TokenWor, TokenFltr> {

  private final JwtUtil jwtUtil;
  private final ObjectMapper om;

  @Transactional
  public TokenDecoded extract
  (HttpServletRequest servletRequest, Boolean isRefresh) {
    TokenDecoded result = null;
    if(servletRequest.getContentLength() > 0) {
      String body = RequestUtil.getBody(servletRequest);
      AbstractRequest request = null;
      try {
        request = om.readValue(body, AbstractRequest.class);
      }
      catch(JsonProcessingException ex) {
      }
      if(request != null && request.token != null) {
        try {
          result = jwtUtil.decode(request.token);
        }
        catch(JwtException | IllegalArgumentException ex) {
        }
        if(!isValid(result, isRefresh)) {
          result = null;
        }
      }
    }
    return result;
  }

  @Transactional
  public TokenInfo generateAccess
  (Long userId, String username, Collection<String> groups) {
    revokeAccess(userId);
    LocalDateTime issued = LocalDateTime.now();
    TokenWor tokenRecord = new TokenWor();
    tokenRecord.setUserId(userId);
    tokenRecord.setExpired(jwtUtil.expiredAccess(issued));
    tokenRecord.setIsRefresh(Boolean.FALSE);
    tokenRecord.setGroups(String.join(Token.GROUPS_DELIMITER, groups));
    em.persist(tokenRecord);
    TokenDecoded accessTokenDecoded = jwtUtil.generate
    (tokenRecord.getId(), userId, username, issued, groups);
    return accessTokenDecoded.info();
  }

  public List<TokenInfo> generateAll
  (Long userId, String username, Collection<String> groups) {
    LocalDateTime issued = LocalDateTime.now();
    TokenWor tokenRecord = new TokenWor();
    tokenRecord.setUserId(userId);
    tokenRecord.setExpired(jwtUtil.expiredAccess(issued));
    tokenRecord.setIsRefresh(Boolean.TRUE);
    tokenRecord.setGroups(String.join(Token.GROUPS_DELIMITER, groups));
    em.persist(tokenRecord);
    TokenDecoded accessTokenDecoded = jwtUtil.generate
    (tokenRecord.getId(), userId, username, issued, groups);
    TokenWor refreshTokenRecord = new TokenWor();
    refreshTokenRecord.setUserId(userId);
    refreshTokenRecord.setExpired(jwtUtil.expiredRefresh(issued));
    refreshTokenRecord.setIsRefresh(true);
    refreshTokenRecord.setGroups("");
    em.persist(refreshTokenRecord);
    TokenDecoded refreshTokenDecoded = jwtUtil.generateRefresh
    (refreshTokenRecord.getId(), userId, username, issued);
    return List.of(accessTokenDecoded.info(), refreshTokenDecoded.info());
  }

  @Transactional
  public boolean isValid(TokenDecoded tokenDecoded, Boolean isRefresh) {
    boolean result = false;
    if(tokenDecoded != null && tokenDecoded.id != null
    && (isRefresh == null || tokenDecoded.isRefresh == isRefresh)) {
      CriteriaBuilder cb = em.getCriteriaBuilder();
      CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
      Root<TokenWor> root = cq.from(TokenWor.class);
      cq.select(root.get(TokenWor.ID));
      Predicate where = cb.equal(root.get(TokenWor.ID), tokenDecoded.id);
      if(isRefresh != null) {
        where = cb.and(where, cb.equal(root.get(TokenWor.IS_REFRESH), isRefresh));
      }
      cq.where(where);
      result = !em.createQuery(cq).getResultList().isEmpty();
    }
    return result;
  }

  @Transactional
  public int revokeAccess(long userId) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaDelete<TokenWor> crd = cb.createCriteriaDelete(TokenWor.class);
    Root<TokenWor> root = crd.getRoot();
    crd.where(cb.and(
      cb.equal(root.get(TokenWor.USER_ID), userId)
    , cb.equal(root.get(TokenWor.IS_REFRESH), false)
    ));
    return em.createQuery(crd).executeUpdate();
  }

  @Transactional
  public int revokeAll(long userId) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaDelete<TokenWor> crd = cb.createCriteriaDelete(TokenWor.class);
    crd.where(cb.equal(crd.getRoot().get(TokenWor.USER_ID), userId));
    return em.createQuery(crd).executeUpdate();
  }

}
