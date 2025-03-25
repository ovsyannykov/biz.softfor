package biz.softfor.spring.rest.errorhandling;

import biz.softfor.i18nspring.I18n;
import biz.softfor.util.StringUtil;
import biz.softfor.util.api.BasicResponse;
import biz.softfor.util.api.ClientError;
import biz.softfor.util.api.ErrorData;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ErrorControllerAdvice {

  private final static String[] IGNORED = {
    "org.apache.catalina.connector.ClientAbortException"
  };

  @Autowired
  private I18n i18n;

  public static String extractName(String namePath, I18n i18n) {
    return i18n.message(StringUtil.fieldToName(namePath)) + ": ";
  }

  static Exception validationException2ClientError(Exception ex, I18n i18n) {
    Exception result = ex;
    if(ex instanceof MethodArgumentNotValidException ve) {
      String msg = "";
      for(FieldError fe : ve.getBindingResult().getFieldErrors()) {
        if(!msg.isEmpty()) {
          msg += "; ";
        }
        msg += extractName(fe.getField(), i18n) + fe.getDefaultMessage();
      }
      result = new ClientError(msg, ex, BasicResponse.BAD_REQUEST);
    } else if(ex instanceof ConstraintViolationException ce) {
      String msg = "";
      for(ConstraintViolation<?> cv : ce.getConstraintViolations()) {
        if(!msg.isEmpty()) {
          msg += "; ";
        }
        msg += extractName(cv.getPropertyPath().toString(), i18n) + i18n.message(cv.getMessage());
      }
      result = new ClientError(msg, ex, BasicResponse.BAD_REQUEST);
    }
    return result;
  }

  @ExceptionHandler({ Exception.class })
  @ResponseBody
  public BasicResponse<?> handle(Exception ex, HttpServletRequest request) {
    BasicResponse<?> result = null;
    if(ex == null || !ArrayUtils.contains(IGNORED, ex.getClass().getName())) {
      ex = validationException2ClientError(ex, i18n);
      result = new BasicResponse<>(new ErrorData(ex, request));
    }
    return result;
  }

}
