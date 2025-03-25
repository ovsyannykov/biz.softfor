package biz.softfor.vaadin.security;

import biz.softfor.vaadin.VaadinUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler
implements AuthenticationSuccessHandler {

  @Override
  protected String determineTargetUrl
  (HttpServletRequest request, HttpServletResponse response) {
    String result = request.getParameter(VaadinUtil.RETPATH);
    if(StringUtils.isBlank(result)) {
      result = super.determineTargetUrl(request, response);
    }
    return result;
  }

}
