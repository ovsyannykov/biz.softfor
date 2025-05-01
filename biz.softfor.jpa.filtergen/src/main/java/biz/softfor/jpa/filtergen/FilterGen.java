package biz.softfor.jpa.filtergen;

import biz.softfor.codegen.CodeGen;
import biz.softfor.codegen.CodeGenUtil;
import biz.softfor.util.Range;
import biz.softfor.util.Reflection;
import biz.softfor.util.api.Identifiable;
import biz.softfor.util.api.filter.FilterId;
import biz.softfor.util.security.ActionAccess;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.lang.model.element.Modifier;
import org.hibernate.type.Type;
import org.hibernate.validator.constraints.URL;

public class FilterGen extends CodeGen {

  private final static String[] FILTER_EXCLUDED_PACKAGES
  = { Entity.class.getPackageName(), CodeGenUtil.VALIDATION_ANNOTATIONS_PKG };
  private final static String[] FILTER_FIELD_EXCLUDED_ANNOTATIONS
  = { ActionAccess.class.getName(), Type.class.getName(), URL.class.getName() };
  private final static String RESET_METHOD = "reset";

  public FilterGen() {
    super(GenFilter.class);
  }

  @Override
  public void process(Class<?> clazz) throws IllegalAccessException
  , IllegalArgumentException, InvocationTargetException, NoSuchFieldException
  , NoSuchMethodException, SecurityException {
    if(!CodeGenUtil.isLinkClass(clazz)) {
      Class<?> idClass = Reflection.idClass(clazz);
      String clazzSimpleName = clazz.getSimpleName();
      String clazzPackageName = clazz.getPackageName();
      String apiPackageName = Reflection.apiPackageName(clazzPackageName);
      String filterSimpleName = Reflection.filterClassName(clazzSimpleName);
      TypeSpec.Builder classBldr = TypeSpec.classBuilder(filterSimpleName)
      .addAnnotation(CodeGenUtil.generated(clazz.getName()))
      .addAnnotations(CodeGenUtil.copyAnnotations(clazz.getAnnotations()
      , CodeGenUtil.API_EXCLUDED_PACKAGES
      , CodeGenUtil.API_EXCLUDED_ANNOTATIONS
      , null
      ));
      classBldr.superclass
      (ParameterizedTypeName.get(FilterId.class, idClass));
      CodeGenUtil.addToStringAndEqualsAndHashCode(classBldr, true);

      MethodSpec.Builder resetMethod = MethodSpec.methodBuilder(RESET_METHOD)
      .addStatement("super." + RESET_METHOD + "()")
      .addModifiers(Modifier.PUBLIC);

      for(Field dclField : clazz.getDeclaredFields()) {
        String fieldName = dclField.getName();
        if(Reflection.isProperty(dclField.getModifiers())
        && !Identifiable.ID.equals(fieldName)) {
          Class<?> dclClass = dclField.getType();
          TypeName fieldType;
          resetMethod.addStatement("$N = null", fieldName);
          if(CodeGenUtil.isAnnotationPresent(dclField, OneToOne.class)
          || CodeGenUtil.isAnnotationPresent(dclField, ManyToOne.class)) {
            fieldType = CodeGenUtil.filterTypeName(dclClass);
          } else if(CodeGenUtil.isAnnotationPresent(dclField, OneToMany.class)
          || CodeGenUtil.isAnnotationPresent(dclField, ManyToMany.class)) {
            Class<?> joinClass = Reflection.genericParameter(dclField);
            fieldType = CodeGenUtil.filterTypeName(joinClass);
          } else {
            fieldType = TypeName.get(dclClass);
            if(LocalDate.class.isAssignableFrom(dclClass)
            || LocalDateTime.class.isAssignableFrom(dclClass)) {
              fieldType
              = ParameterizedTypeName.get(ClassName.get(Range.class), fieldType);
            } else if(Number.class.isAssignableFrom(dclClass)
            || Enum.class.isAssignableFrom(dclClass)) {
              fieldType
              = addAssignMethod(classBldr, fieldType, fieldName, List.class);
            }
          }
          FieldSpec.Builder fieldBldr = CodeGenUtil.fieldBuilder(
            dclField
          , fieldName
          , fieldType
          , FILTER_EXCLUDED_PACKAGES
          , FILTER_FIELD_EXCLUDED_ANNOTATIONS
          );
          CodeGenUtil.addField(classBldr, fieldBldr, fieldType, fieldName, false);
        }
      }
      classBldr.addMethod(resetMethod.build());
      CodeGenUtil.writeSrc(classBldr, apiPackageName, processingEnv);
    }
  }

  private static TypeName addAssignMethod(
    TypeSpec.Builder filterBldr
  , TypeName fieldTypeName
  , String fieldName
  , Class<?> collectionClass
  ) {
    MethodSpec.Builder assignMethod
    = MethodSpec.methodBuilder(CodeGenUtil.fieldMethodName("assign", fieldName))
    .addParameter(ArrayTypeName.of(fieldTypeName), fieldName).varargs()
    .addStatement("this.$N = $T.of($N)", fieldName, collectionClass, fieldName)
    .addModifiers(Modifier.PUBLIC);
    filterBldr.addMethod(assignMethod.build());
    return ParameterizedTypeName.get(ClassName.get(collectionClass), fieldTypeName);
  }

}
