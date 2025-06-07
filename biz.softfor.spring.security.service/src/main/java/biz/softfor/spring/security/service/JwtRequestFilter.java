package biz.softfor.spring.security.service;

import biz.softfor.spring.security.SecurityUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

  private final TokenSvc tokenSvc;

  @Override
  protected void doFilterInternal(
    HttpServletRequest servletRequest
  , HttpServletResponse servletResponse
  , FilterChain filterChain
  ) throws ServletException, IOException {
    if(SecurityUtil.getAuthentication() == null) {
      TokenDecoded tokenDecoded = tokenSvc.extract(servletRequest, null);
      if(tokenDecoded != null) {
        UsernamePasswordAuthenticationToken authToken
        = new UsernamePasswordAuthenticationToken(
          tokenDecoded.username
        , null
        , SecurityUtil.authorities(tokenDecoded.groups)
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }
    filterChain.doFilter(servletRequest, servletResponse);
  }

}
