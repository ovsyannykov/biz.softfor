package biz.softfor.jpa.crud.querygraph;

import biz.softfor.util.Reflection;
import biz.softfor.util.StringUtil;
import biz.softfor.util.api.ServerError;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import java.lang.reflect.Field;
import org.apache.commons.lang3.StringUtils;

public final class ManyToManyInf {

  public final String fieldName;
  public final String table;
  public final String joinColumn;
  public final Class joinClass;
  public final String joinFieldName;
  public final String inverseJoinColumn;
  public final Class inverseJoinClass;
  public final String inverseJoinFieldName;
  public final String className;
  public final String packageName;

  public static Field mappedField(Field field) {
    Field result = null;
    Class joinClass = Reflection.genericParameter(field);
    String mappedBy = field.getAnnotation(ManyToMany.class).mappedBy();
    if(StringUtils.isBlank(mappedBy)) {
      Class<?> fieldClass = field.getDeclaringClass();
      for(Field f : Reflection.annotatedFields(joinClass, ManyToMany.class)) {
        mappedBy = f.getAnnotation(ManyToMany.class).mappedBy();
        if(mappedBy.equals(field.getName())
        && fieldClass == Reflection.genericParameter(f)) {
          result = f;
          break;
        }
      }
    } else {
      result = Reflection.declaredField(joinClass, mappedBy);
    }
    return result;
  }

  public ManyToManyInf(Field field) {
    JoinColumn[] joinColumns;
    JoinColumn[] inverseJoinColumns;
    JoinTable joinTable = field.getAnnotation(JoinTable.class);
    if(joinTable == null) {
      Field mappedField = mappedField(field);
      if(mappedField == null) {
        throw new ServerError("No ManyToMany mapped field for "
        + field.getType().getName() + "#" + field.getName());
      }
      joinTable = mappedField.getAnnotation(JoinTable.class);
      if(joinTable == null) {
        throw new ServerError(
        "Field " + field.getType().getName() + "#" + field.getName()
        + " annotated with @ManyToMany does not have @JoinTable annotation.");
      }
      joinColumns = joinTable.inverseJoinColumns();
      joinClass = mappedField.getDeclaringClass();
      inverseJoinColumns = joinTable.joinColumns();
      inverseJoinClass = Reflection.genericParameter(mappedField);
    } else {
      joinColumns = joinTable.joinColumns();
      joinClass = field.getDeclaringClass();
      inverseJoinColumns = joinTable.inverseJoinColumns();
      inverseJoinClass = Reflection.genericParameter(field);
    }
    table = joinTable.name();
    fieldName = table.toLowerCase();
    joinColumn = joinColumns[0].name();
    joinFieldName = StringUtils.uncapitalize(joinClass.getSimpleName());
    inverseJoinColumn = inverseJoinColumns[0].name();
    inverseJoinFieldName
    = StringUtils.uncapitalize(inverseJoinClass.getSimpleName());
    packageName = inverseJoinClass.getPackageName();
    className = StringUtil.snakeCaseCapitalize(table);
  }

  public final Class<?> linkClass() throws ClassNotFoundException {
    return Class.forName(StringUtil.field(packageName, className));
  }

}
