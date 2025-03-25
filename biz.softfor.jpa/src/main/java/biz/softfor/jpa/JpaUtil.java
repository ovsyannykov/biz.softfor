package biz.softfor.jpa;

import biz.softfor.util.Reflection;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import java.lang.reflect.Field;

public class JpaUtil {

  public static Join<?, ?> joinTo(From<?, ?> from, String attrName) {
    Join<?, ?> result = null;
    for(Join<?, ?> j : from.getJoins()) {
      if(attrName.equals(j.getAttribute().getName())) {
        result = j;
        break;
      }
    }
    if(result == null) {
      result = from.join(attrName, JoinType.LEFT);
    }
    return result;
  }

  public static String oneToManyJoinColumnName(Field field) {
    JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
    if(joinColumn == null) {
      String mappedBy = field.getAnnotation(OneToMany.class).mappedBy();
      Class<?> joinClass = Reflection.genericParameter(field);
      Field joinField = Reflection.declaredField(joinClass, mappedBy);
      joinColumn = joinField.getAnnotation(JoinColumn.class);
    }
    return joinColumn.name();
  }

}
