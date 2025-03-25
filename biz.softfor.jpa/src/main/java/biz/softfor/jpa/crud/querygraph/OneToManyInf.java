package biz.softfor.jpa.crud.querygraph;

import biz.softfor.jpa.JpaUtil;
import biz.softfor.util.Reflection;
import biz.softfor.util.api.Identifiable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import java.lang.reflect.Field;

class OneToManyInf {

  public final String joinColumnName;
  public final Class<Identifiable<? extends Number>> joinClass;
  public final ColumnDescr joinCd;
  public final boolean orphanRemoval;

  OneToManyInf(Field field) {
    joinColumnName = JpaUtil.oneToManyJoinColumnName(field);
    joinClass = Reflection.genericParameter(field);
    JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
    if(joinColumn == null) {
      String joinFieldName = field.getAnnotation(OneToMany.class).mappedBy();
      Field joinField = Reflection.declaredField(joinClass, joinFieldName);
      joinCd = new ManyToOneColumnDescr(joinClass, joinField);
    } else {
      String joinFieldName = joinColumn.name();
      Field joinField = Reflection.declaredField(joinClass, joinFieldName);
      joinCd = new ColumnDescr(joinClass, joinField);
    }
    orphanRemoval = field.getAnnotation(OneToMany.class).orphanRemoval();
  }

  final Class<?> worJoinClass() throws ClassNotFoundException {
    Class<?> result;
    if(joinClass.isAnnotationPresent(ManyToManyGeneratedLink.class)) {
      result = joinClass;
    } else {
      result = Class.forName(Reflection.worClassName(joinClass.getName()));
    }
    return result;
  }
}
