package biz.softfor.user.spring;

import biz.softfor.user.jpa.Role;
import biz.softfor.util.Constants;
import biz.softfor.util.api.ServerError;
import biz.softfor.util.security.RoleCalc;
import biz.softfor.util.security.ClassRoleCalc;
import biz.softfor.util.security.MethodRoleCalc;
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
import org.springframework.stereotype.Service;

@Component
public class ServicesScanRoles implements ScanRoles {

  @Override
  public void load(
    Map<Long, Role> fromCode
  , Map<Long, ParentRoles> member2Parent
  , Map<Long, List<Long>> parent2Members
  ) {
    String filename = Service.class.getSimpleName() + Constants.REFLECTIONS_EXT;
    try(InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filename);
    InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
    BufferedReader reader = new BufferedReader(streamReader)) {
      String line;
      while((line = reader.readLine()) != null) {
        Class<?> clazz = Class.forName(line);
        RoleCalc classCalc = new ClassRoleCalc(clazz);
        if(!classCalc.ignore()) {
          Role role = new Role(classCalc);
          Long roleId = role.getId();
          fromCode.put(roleId, role);
          List<Long> children = new ArrayList<>();
          parent2Members.put(roleId, children);
          for(Method method : clazz.getMethods()) {
            RoleCalc calc
            = new MethodRoleCalc(clazz, method, MethodRoleCalc.SERVICE);
            if(!calc.ignore()) {
              Role methodRole = new Role(calc);
              Long methodRoleId = methodRole.getId();
              fromCode.put(methodRoleId, methodRole);
              member2Parent.put(methodRoleId, new ParentRoles(role.getId()));
              children.add(methodRoleId);
            }
          }
        }
      }
    }
    catch(ClassNotFoundException | IOException e) {
      throw new ServerError(e);
    }
  }

}
