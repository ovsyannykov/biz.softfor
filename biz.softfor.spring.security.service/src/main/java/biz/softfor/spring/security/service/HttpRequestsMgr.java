package biz.softfor.spring.security.service;

import biz.softfor.spring.security.SecurityUtil;
import biz.softfor.user.spring.RoleData;
import biz.softfor.user.spring.SecurityMgr;
import static biz.softfor.util.security.DefaultAccess.AUTHORIZED;
import static biz.softfor.util.security.DefaultAccess.EVERYBODY;
import static biz.softfor.util.security.DefaultAccess.NOBODY;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

@Component
public class HttpRequestsMgr {

  private final SecurityMgr securityMgr;
  private final Map<String, RoleData> paths;

  @AllArgsConstructor
  private static class MatchParams {

    final HttpMethod httpMethod;
    final String[] paths;

  }

  public HttpRequestsMgr(SecurityMgr securityMgr) {
    this.securityMgr = securityMgr;
    paths = new HashMap<>();
    for(RoleData rd : securityMgr.rolesData.values()) {
      if(rd.isUrl) {
        List<MatchParams> rmps = new ArrayList<>();
        String[] params = rd.objName.split(UrlRoleCalc.MATCH_DELIMITER);
        for(String p : params) {
          String[] matchArgs = p.split(UrlRoleCalc.HTTPMETHOD_DELIMITER);
          HttpMethod httpMethod;
          int iMatchArgs;
          if(matchArgs.length > 1) {
            httpMethod = HttpMethod.valueOf(matchArgs[0]);
            iMatchArgs = 1;
          } else {
            httpMethod = null;
            iMatchArgs = 0;
          }
          String[] ps = matchArgs[iMatchArgs].split(UrlRoleCalc.PATH_DELIMITER);
          rmps.add(new MatchParams(httpMethod, ps));
        }
        for(MatchParams rmp : rmps) {
          if(rmp.httpMethod == null) {
            for(String p : rmp.paths) {
              paths.put(p, rd);
            }
          } else {
            for(String p : rmp.paths) {
              paths.put(UrlRoleCalc.path(rmp.httpMethod.name(), p), rd);
            }
          }
        }
      }
    }
    if(SecurityMgr.DEBUG) {
      System.out.println("\nAuthorized HTTP requests: " + paths.size()
      + "\n" + "=".repeat(16));
      if(!paths.isEmpty()) {
        String result = "";
        for(Map.Entry<String, RoleData> e : paths.entrySet()) {
          result += "\n" + e.getKey() + ": " + e.getValue();
        }
        System.out.println(result);
      }
    }
  }

  public AuthorizationDecision check
  (Supplier<Authentication> authentication, RequestAuthorizationContext ctx) {
    boolean result;
    RoleData rd = get(ctx.getRequest());
    if(rd.effGroups.isEmpty()) {
      result = switch(rd.effDefaultAccess) {
        case EVERYBODY -> true;
        case AUTHORIZED -> SecurityUtil.isAuthorized(authentication.get());
        case NOBODY -> false;
      };
    } else {
      Collection<String> groups
      = SecurityUtil.groups(authentication.get().getAuthorities());
      result = securityMgr.isAllowed(rd.id, groups);
    }
    return new AuthorizationDecision(result);
  }

  public boolean matches(HttpServletRequest r) {
    return get(r) != null;
  }

  private RoleData get(HttpServletRequest httpServletRequest) {
    String p = httpServletRequest.getRequestURI();
    RoleData result = paths.get(p);
    if(result == null) {
      result = paths.get(UrlRoleCalc.path(httpServletRequest.getMethod(), p));
    }
    return result;
  }

}
