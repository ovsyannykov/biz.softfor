package biz.softfor.user.spring;

import biz.softfor.user.jpa.Role;
import biz.softfor.util.Generated;
import biz.softfor.util.Reflection;
import biz.softfor.util.security.AbstractRoleCalc;
import biz.softfor.util.security.ClassRoleCalc;
import biz.softfor.util.security.FieldRoleCalc;
import biz.softfor.util.security.UpdateClassRoleCalc;
import biz.softfor.util.security.UpdateFieldRoleCalc;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.metamodel.EntityType;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log
public class EntitiesScanRoles implements ScanRoles {

  private final EntityManager em;

  @Override
  public void load(
    Map<Long, Role> fromCode
  , Map<Long, ParentRoles> member2Parent
  , Map<Long, List<Long>> parent2Members
  ) {
    for(EntityType<?> e : em.getMetamodel().getEntities()) {
      Class<?> type = e.getJavaType();
      if(!type.isAnnotationPresent(Generated.class)) {
        AbstractRoleCalc calc = new ClassRoleCalc(type);
        if(!calc.ignore()) {
          AbstractRoleCalc updateCalc = new UpdateClassRoleCalc(type);
          Role role = new Role(calc);
          Long roleId = role.getId();
          if(!fromCode.containsKey(roleId)) {
            fromCode.put(roleId, role);
            List<Long> children = new ArrayList<>();
            parent2Members.put(roleId, children);

            Role updateRole = new Role(updateCalc);
            Long updateRoleId = updateRole.getId();
            fromCode.put(updateRoleId, updateRole);
            List<Long> updateChildren = new ArrayList<>();
            parent2Members.put(updateRoleId, updateChildren);
            for(Field field : Reflection.declaredProperties(type)) {
              AbstractRoleCalc fieldCalc = new FieldRoleCalc(field);
              if(!fieldCalc.ignore()) {
                AbstractRoleCalc updateFieldCalc = new UpdateFieldRoleCalc(field);
                Role fieldRole = new Role(fieldCalc);
                Long fieldRoleId = fieldRole.getId();
                fromCode.put(fieldRoleId, fieldRole);
                children.add(fieldRoleId);
                Role fieldUpdateRole = new Role(updateFieldCalc);
                Long fieldUpdateRoleId = fieldUpdateRole.getId();
                fromCode.put(fieldUpdateRoleId, fieldUpdateRole);
                updateChildren.add(fieldUpdateRoleId);
                ParentRoles parentRoles;
                ParentRoles updateParentRoles;
                boolean isOneToOne = field.isAnnotationPresent(OneToOne.class);
                boolean isManyToOne = field.isAnnotationPresent(ManyToOne.class);
                boolean isOneToMany = field.isAnnotationPresent(OneToMany.class);
                boolean isManyToMany = field.isAnnotationPresent(ManyToMany.class);
                if(isOneToOne || isManyToOne || isOneToMany || isManyToMany) {
                  Class<?> fieldType = (isOneToOne || isManyToOne) ? field.getType()
                  : Reflection.genericParameter(field);
                  AbstractRoleCalc fieldTypeCalc = new ClassRoleCalc(fieldType);
                  AbstractRoleCalc updateFieldTypeCalc
                  = new UpdateClassRoleCalc(fieldType);
                  parentRoles = new ParentRoles(role.getId(), fieldTypeCalc.id());
                  updateParentRoles
                  = new ParentRoles(updateRole.getId(), updateFieldTypeCalc.id());
                } else {
                  parentRoles = new ParentRoles(role.getId());
                  updateParentRoles = new ParentRoles(updateRole.getId());
                }
                member2Parent.put(fieldRoleId, parentRoles);
                member2Parent.put(fieldUpdateRoleId, updateParentRoles);
              }
            }
          }
        }
      }
    }
  }

}
