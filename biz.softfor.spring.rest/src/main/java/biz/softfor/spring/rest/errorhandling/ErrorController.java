package biz.softfor.spring.rest.errorhandling;

import biz.softfor.spring.messagesi18n.I18n;
import biz.softfor.util.api.BasicResponse;
import biz.softfor.util.api.ErrorData;
import biz.softfor.util.api.StdPath;
import biz.softfor.util.security.IgnoreAccess;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.java.Log;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@IgnoreAccess
@RestController
@Log
public class ErrorController extends AbstractErrorController {

  private final I18n i18n;

  public ErrorController(ErrorAttributes errorAttributes, I18n i18n) {
    super(errorAttributes);
    this.i18n = i18n;
  }

  @RequestMapping(path = StdPath.ERROR, method = { RequestMethod.GET, RequestMethod.POST }, produces = MediaType.APPLICATION_JSON_VALUE)
  public BasicResponse<?> handle(HttpServletRequest request) {
    Exception ex = ErrorControllerAdvice.validationException2ClientError
    ((Exception)request.getAttribute(RequestDispatcher.ERROR_EXCEPTION), i18n);
    return new BasicResponse(new ErrorData(ex, request));
  }

}
