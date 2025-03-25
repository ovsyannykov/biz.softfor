package biz.softfor.user.spring.rest;

import biz.softfor.spring.security.service.AuthSvc;
import biz.softfor.user.jpa.UserRequest;
import biz.softfor.user.jpa.UserWor;
import biz.softfor.util.Create;
import biz.softfor.util.api.AbstractRequest;
import biz.softfor.util.api.AuthResponse;
import biz.softfor.util.api.CommonResponse;
import biz.softfor.util.api.StdPath;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AuthCtlr {

  private final AuthSvc authSvc;

  @RequestMapping(path = StdPath.LOGIN, method = RequestMethod.POST)
  public AuthResponse auth(
    @RequestBody @Validated({ UserWor.Username.class, UserWor.Password.class })
    UserRequest.Create request
  ) throws IllegalAccessException, InvocationTargetException
  , InstantiationException, NoSuchMethodException {
    return new AuthResponse(authSvc.auth(request));
  }

  @RequestMapping(path = StdPath.REFRESH_TOKEN, method = RequestMethod.POST)
  public AuthResponse refreshToken(@RequestBody AbstractRequest request) {
    return new AuthResponse(List.of(authSvc.refreshToken(request)));
  }

  @RequestMapping(path = StdPath.REGISTRATION, method = RequestMethod.POST)
  public CommonResponse<UserWor> registration
  (@RequestBody @Validated(Create.class) UserRequest.Create request) {
    CommonResponse<UserWor> result = authSvc.registration(request);
    if(CollectionUtils.isNotEmpty(result.getData())) {
      result.getData(0).setPassword(null);
    }
    return result;
  }

}
