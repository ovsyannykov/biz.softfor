package biz.softfor.spring.security.service;

import biz.softfor.user.jpa.Role;
import biz.softfor.user.spring.ParentRoles;
import biz.softfor.user.spring.ScanRoles;
import biz.softfor.util.Constants;
import biz.softfor.util.api.ServerError;
import biz.softfor.util.security.RoleCalc;
import biz.softfor.util.security.AbstractRoleCalc;
import biz.softfor.util.security.ClassRoleCalc;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

@Component
public class RestControllerScanRoles implements ScanRoles {

  @Override
  public void load(
    Map<Long, Role> fromCode
  , Map<Long, ParentRoles> member2Parent
  , Map<Long, List<Long>> parent2Members
  ) {
    String filename = RestController.class.getSimpleName() + Constants.REFLECTIONS_EXT;
    try(InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filename);
    InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
    BufferedReader reader = new BufferedReader(streamReader)) {
      String line;
      while((line = reader.readLine()) != null) {
        Class<?> clazz = Class.forName(line);
        RoleCalc classCalc = new ClassRoleCalc(clazz);
        if(!classCalc.ignore()) {
          Role classRole = new Role(classCalc);
          Long classRoleId = classRole.getId();
          List<Long> children = new ArrayList<>();
          parent2Members.put(classRoleId, children);
          for(Method method : clazz.getMethods()) {
            AbstractRoleCalc calc = new UrlRoleCalc(clazz, method);
            if(!calc.ignore()) {
              Role role = new Role(calc);
              Long roleId = role.getId();
              fromCode.put(roleId, role);
              children.add(roleId);
            }
          }
          if(!children.isEmpty()) {
            fromCode.put(classRoleId, classRole);
            for(Long childId : children) {
              member2Parent.put(childId, new ParentRoles(classRoleId));
            }
          }
        }
      }
    }
    catch(ClassNotFoundException | IOException ex) {
      throw new ServerError(ex);
    }
  }

}
