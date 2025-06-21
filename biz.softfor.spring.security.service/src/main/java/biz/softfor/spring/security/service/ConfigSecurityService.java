package biz.softfor.spring.security.service;

import biz.softfor.spring.rest.cachedbodyrequestfilter.CachedBodyRequestFilter;
import biz.softfor.util.api.ClientError;
import biz.softfor.util.api.StdPath;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@Configuration
@EnableMethodSecurity
@ComponentScan
public class ConfigSecurityService {

  @Bean
  public SecurityFilterChain securityFilterChain(
    HttpSecurity http
  , LogoutHandler logoutHandler
  , JwtRequestFilter jwtRequestFilter
  , CachedBodyRequestFilter cachedBodyRequestFilter
  , HttpRequestsMgr requestsMgr
  ) throws Exception {
    return http
    .csrf(AbstractHttpConfigurer::disable)
    .cors(AbstractHttpConfigurer::disable)
    .sessionManagement(customizer -> customizer.sessionCreationPolicy
    (SessionCreationPolicy.STATELESS))
    .exceptionHandling(customizer -> customizer.authenticationEntryPoint
    (new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
    .authorizeHttpRequests(auth -> {
      try {
        auth
        .requestMatchers(StdPath.LOGOUT).authenticated()
        .requestMatchers(StdPath.REFRESH_TOKEN).authenticated()
        .requestMatchers(requestsMgr::matches).access(requestsMgr::check)
        .anyRequest().permitAll();
      }
      catch(Exception ex) {
        throw new ClientError(ex);
      }
    })
    .logout(logout -> logout
      .addLogoutHandler(logoutHandler)
      .logoutSuccessHandler((request, response, authentication) -> {
        response.setStatus(HttpServletResponse.SC_OK);
      })
    )
    .addFilterBefore
    (cachedBodyRequestFilter, UsernamePasswordAuthenticationFilter.class)
    .addFilterAfter(jwtRequestFilter, CachedBodyRequestFilter.class)
    .build();
  }

}
