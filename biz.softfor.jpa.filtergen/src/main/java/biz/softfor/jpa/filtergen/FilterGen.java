package biz.softfor.jpa.filtergen;

import biz.softfor.codegen.CodeGen;
import biz.softfor.codegen.CodeGenUtil;
import biz.softfor.jpa.crud.querygraph.ColumnDescr;
import biz.softfor.util.Range;
import biz.softfor.util.Reflection;
import biz.softfor.util.api.Identifiable;
import biz.softfor.util.api.filter.FilterId;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Modifier;

public class FilterGen extends CodeGen {

  private final static String RESET_METHOD = "reset";

  protected FilterGen(Class<?> classWithProcessingEntities) {
    super(GenFilter.class.getName(), classWithProcessingEntities);
  }

  @Override
  public void process(Class<?> clazz) {
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
      .addStatement("super.reset()")
      .addModifiers(Modifier.PUBLIC);

      for(Field dclField : clazz.getDeclaredFields()) {
        if(Reflection.isProperty(dclField.getModifiers())
        && !dclField.getName().equals(Identifiable.ID)) {
          Class<?> dclClass = dclField.getType();
          String fieldName = dclField.getName();
          TypeName fieldType;
          String keyName;
          TypeName keyTypeName;
          FieldSpec.Builder keyBldr;
          resetMethod.addStatement("$N = null", fieldName);
          if(dclField.isAnnotationPresent(OneToOne.class)) {
            fieldType = CodeGenUtil.filterTypeName(dclClass);
          } else if(dclField.isAnnotationPresent(ManyToOne.class)) {
            fieldType = CodeGenUtil.filterTypeName(dclClass);
            Class idDeclClass = Reflection.idClass(dclClass);
            keyName = ColumnDescr.manyToOneKeyName(dclField);
            keyTypeName = addAssignMethod
            (classBldr, TypeName.get(idDeclClass), keyName, Set.class);
            keyBldr = FieldSpec.builder(keyTypeName, keyName)
            .addModifiers(Modifier.PRIVATE);
            CodeGenUtil.addField(classBldr, keyBldr, keyTypeName, keyName, false);
            resetMethod.addStatement("$N = null", keyName);
          } else if(dclField.isAnnotationPresent(OneToMany.class)
          || dclField.isAnnotationPresent(ManyToMany.class)) {
            Class<?> joinClass = Reflection.genericParameter(dclField);
            fieldType = CodeGenUtil.filterTypeName(joinClass);
            Class<?> joinIdClass = Reflection.idClass(joinClass);
            keyName = ColumnDescr.toManyKeyName(fieldName);
            Class<?> collectionClass
            = dclField.isAnnotationPresent(OneToMany.class)
            ? List.class : Set.class;
            keyTypeName = addAssignMethod
            (classBldr, TypeName.get(joinIdClass), keyName, collectionClass);
            keyBldr = FieldSpec.builder(keyTypeName, keyName)
            .addModifiers(Modifier.PRIVATE);
            CodeGenUtil.addField(classBldr, keyBldr, keyTypeName, keyName, false);
            resetMethod.addStatement("$N = null", keyName);
          } else {
            fieldType = TypeName.get(dclClass);
            if(LocalDate.class.isAssignableFrom(dclClass)
            || LocalDateTime.class.isAssignableFrom(dclClass)) {
              fieldType
              = ParameterizedTypeName.get(ClassName.get(Range.class), fieldType);
            } else if(Number.class.isAssignableFrom(dclClass)
            || Enum.class.isAssignableFrom(dclClass)) {
              fieldType
              = addAssignMethod(classBldr, fieldType, fieldName, Set.class);
            }
          }
          FieldSpec.Builder fieldBldr = CodeGenUtil.fieldBuilder(
            dclField
          , fieldName
          , fieldType
          , CodeGenUtil.FILTER_EXCLUDED_PACKAGES
          , CodeGenUtil.FILTER_FIELD_EXCLUDED_ANNOTATIONS
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
