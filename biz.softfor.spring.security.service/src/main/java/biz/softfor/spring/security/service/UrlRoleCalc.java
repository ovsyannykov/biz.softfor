package biz.softfor.spring.security.service;

import biz.softfor.util.security.MethodRoleCalc;
import java.lang.reflect.Method;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public class UrlRoleCalc extends MethodRoleCalc {

  public final static String ENDPOINT = "{0} (endpoint)";
  public final static String PATH_DELIMITER = ",";
  public final static String MATCH_DELIMITER = ";";
  public final static String HTTPMETHOD_DELIMITER = ":";

  private final RequestMapping rm;

  public static String path(String httpMethod, String urlPath) {
    String result;
    if(httpMethod == null) {
      result = urlPath;
    } else {
      result = httpMethod + HTTPMETHOD_DELIMITER + urlPath;
    }
    return result;
  }

  public UrlRoleCalc(Class<?> parent, Method v) {
    super(parent, v, ENDPOINT);
    rm = o.getAnnotation(RequestMapping.class);
  }

  @Override
  public boolean ignore() {
    return rm == null || super.ignore();
  }

  @Override
  public boolean isUrl() {
    return true;
  }

  @Override
  public String objName() {
    RequestMapping rmClass = p.getAnnotation(RequestMapping.class);
    String result;
    if(rm == null) {
      result = "";
    } else {
      String[] rmPaths = rm.path();
      if(rmPaths == null) {
        rmPaths = rm.value();
      }
      if(rmClass != null) {
        String[] rmClassPaths = rmClass.path();
        if(rmClassPaths == null) {
          rmClassPaths = rmClass.value();
        }
        if(rmClassPaths.length > 0) {
          for(int i = 0; i < rmPaths.length; ++i) {
            rmPaths[i] = rmClassPaths[0] + rmPaths[i];
          }
        }
      }
      String paths = String.join(PATH_DELIMITER, rmPaths);
      RequestMethod[] httpMethods = rm.method();
      if(httpMethods.length == 0) {
        result = paths;
      } else {
        result = "";
        for(RequestMethod m : httpMethods) {
          if(!result.isEmpty()) {
            result += MATCH_DELIMITER;
          }
          result += path(m.name(), paths);
        }
      }
    }
    return result;
  }

}
