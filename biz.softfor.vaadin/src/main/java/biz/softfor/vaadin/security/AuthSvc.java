package biz.softfor.vaadin.security;

import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.VaadinServletResponse;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthSvc {

  public final static String JWT_HEADER_AND_PAYLOAD_COOKIE_NAME = "jwt.headerAndPayload";
  public final static String JWT_SIGNATURE_COOKIE_NAME = "jwt.signature";

  private final AuthenticationContext authenticationContext;

  public void logout() {
    authenticationContext.logout();
    clearCookie(JWT_HEADER_AND_PAYLOAD_COOKIE_NAME, JWT_SIGNATURE_COOKIE_NAME);
  }

  private void clearCookie(String... cookieName) {
    HttpServletRequest request = VaadinServletRequest.getCurrent()
    .getHttpServletRequest();
    HttpServletResponse response = VaadinServletResponse.getCurrent()
    .getHttpServletResponse();
    String contextPath = request.getContextPath();
    if(contextPath.isBlank()) {
      contextPath = "/";
    }
    for(String cn : cookieName) {
      Cookie k = new Cookie(cn, null);
      k.setPath(contextPath);
      k.setMaxAge(0);
      k.setSecure(request.isSecure());
      k.setHttpOnly(false);
      response.addCookie(k);
    }
  }

}
