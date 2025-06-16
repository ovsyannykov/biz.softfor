package biz.softfor.spring.security.service;

import biz.softfor.i18nspring.I18n;
import biz.softfor.spring.security.SecurityUtil;
import biz.softfor.spring.security.UserDetailsEx;
import biz.softfor.user.jpa.User;
import biz.softfor.user.jpa.UserRequest;
import biz.softfor.user.jpa.UserWor;
import biz.softfor.user.jpa.User_;
import biz.softfor.user.spring.UserSvc;
import biz.softfor.util.api.AbstractRequest;
import biz.softfor.util.api.BasicResponse;
import biz.softfor.util.api.ClientError;
import biz.softfor.util.api.CommonResponse;
import biz.softfor.util.api.TokenInfo;
import biz.softfor.util.security.IgnoreAccess;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@IgnoreAccess
@RequiredArgsConstructor
public class AuthSvc implements LogoutHandler {

  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;
  private final PasswordEncoder passwordEncoder;
  private final TokenSvc tokenSvc;
  private final UserDetailsService userDetailsService;
  private final UserSvc userSvc;
  private final I18n i18n;

  @Transactional
  public List<TokenInfo> auth(UserRequest.Create authRequest)
  throws IllegalAccessException, InvocationTargetException
  , InstantiationException, NoSuchMethodException {
    UsernamePasswordAuthenticationToken authToken
    = new UsernamePasswordAuthenticationToken
    (authRequest.data.getUsername(), authRequest.data.getPassword());
    authenticationManager.authenticate(authToken);
    UserDetailsEx details = (UserDetailsEx)
    userDetailsService.loadUserByUsername(authRequest.data.getUsername());
    return tokenSvc.generateAll(
      details.id
    , details.getUsername()
    , SecurityUtil.groups(details.getAuthorities())
    );
  }

  @Override
  public void logout(
    HttpServletRequest servletRequest
  , HttpServletResponse servletResponse
  , Authentication authentication
  ) {
    TokenDecoded tokenDecoded = tokenSvc.extract(servletRequest, Boolean.FALSE);
    if(tokenDecoded == null) {
      throw new ClientError
      (i18n.message(BasicResponse.Access_denied), null, BasicResponse.ACCESS_DENIED);
    } else {
      tokenSvc.revokeAll(tokenDecoded.userId);
      SecurityContextHolder.clearContext();
    }
  }

  public TokenInfo refreshToken(AbstractRequest request) {
    TokenDecoded decodedToken = jwtUtil.decode(request.token);
    if(!tokenSvc.isValid(decodedToken, Boolean.TRUE)) {
      throw new ClientError
      (i18n.message(BasicResponse.Access_denied), null, BasicResponse.ACCESS_DENIED);
    }
    return tokenSvc.generateAccess
    (decodedToken.userId, decodedToken.username, decodedToken.groups);
  }

  public CommonResponse<UserWor> registration(UserRequest.Create request) {
    CommonResponse<UserWor> result;
    if(User.isPasswordValid(request.data.getPassword())) {
      UserRequest.Read readReq = new UserRequest.Read();
      readReq.filter.setUsername(request.data.getUsername());
      readReq.fields = List.of(User_.ID);
      CommonResponse<User> readResponse = userSvc.read(readReq);
      if(CollectionUtils.isEmpty(readResponse.getData())) {
        String encPassword = passwordEncoder.encode(request.data.getPassword());
        request.data.setPassword(encPassword);
        result = userSvc.create(request);
      } else {
        result = new CommonResponse<>(BasicResponse.CLIENT, i18n.message
        (User.User_with_the_given_name_already_exists, request.data.getUsername()));
      }
    } else {
      result = new CommonResponse<>
      (BasicResponse.CLIENT, i18n.message(User.PASSWORD_CONSTRAINTS));
    }
    return result;
  }

}
