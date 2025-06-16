package biz.softfor.jpa.filtergen;

import biz.softfor.codegen.CodeGen;
import biz.softfor.codegen.CodeGenUtil;
import biz.softfor.jpa.Identifier;
import biz.softfor.util.Range;
import biz.softfor.util.Reflection;
import biz.softfor.util.api.Identifiable;
import biz.softfor.util.api.filter.FilterId;
import biz.softfor.util.security.ActionAccess;
import com.palantir.javapoet.ArrayTypeName;
import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.FieldSpec;
import com.palantir.javapoet.MethodSpec;
import com.palantir.javapoet.ParameterizedTypeName;
import com.palantir.javapoet.TypeName;
import com.palantir.javapoet.TypeSpec;
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
  private final static String[] FILTER_FIELD_EXCLUDED_ANNOTATIONS = {
    ActionAccess.class.getName()
  , Identifier.class.getName()
  , Type.class.getName()
  , URL.class.getName()
  };
  private final static String RESET_METHOD = "reset";

  public FilterGen() {
    super(GenFilter.class);
  }

  @Override
  public void process(Class<?> clazz) throws IllegalAccessException
  , IllegalArgumentException, InvocationTargetException, NoSuchFieldException
  , NoSuchMethodException, SecurityException {
    ClassName className = CodeGenUtil.filterClassName(clazz);
    Class<?> idClass = Reflection.idClass(clazz);
    TypeSpec.Builder classBldr = TypeSpec.classBuilder(className)
    .superclass(ParameterizedTypeName.get(FilterId.class, idClass))
    .addAnnotation(CodeGenUtil.generated(clazz.getName()))
    .addAnnotations(CodeGenUtil.copyAnnotations(
      clazz.getAnnotations()
    , CodeGenUtil.API_EXCLUDED_PACKAGES
    , CodeGenUtil.API_EXCLUDED_ANNOTATIONS
    , null
    ));
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
          fieldType = CodeGenUtil.filterClassName(dclClass);
        } else if(CodeGenUtil.isAnnotationPresent(dclField, OneToMany.class)
        || CodeGenUtil.isAnnotationPresent(dclField, ManyToMany.class)) {
          Class<?> joinClass = Reflection.genericParameter(dclField);
          fieldType = CodeGenUtil.filterClassName(joinClass);
        } else {
          fieldType = TypeName.get(dclClass);
          if(Enum.class.isAssignableFrom(dclClass)
          || CodeGenUtil.isAnnotationPresent(dclField, Identifier.class)) {
            classBldr.addMethod(
              MethodSpec.methodBuilder
              (CodeGenUtil.fieldMethodName("assign", fieldName))
              .addModifiers(Modifier.PUBLIC)
              .addParameter(ArrayTypeName.of(fieldType), fieldName).varargs()
              .addStatement
              ("this.$N = $T.of($N)", fieldName, List.class, fieldName)
              .build()
            );
            fieldType
            = ParameterizedTypeName.get(ClassName.get(List.class), fieldType);
          } else if(Number.class.isAssignableFrom(dclClass)
          || LocalDate.class.isAssignableFrom(dclClass)
          || LocalDateTime.class.isAssignableFrom(dclClass)) {
            fieldType
            = ParameterizedTypeName.get(ClassName.get(Range.class), fieldType);
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
    CodeGenUtil.writeSrc(classBldr, className.packageName(), processingEnv);
  }

}
