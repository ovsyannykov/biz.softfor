package biz.softfor.jpa.filtergen;

import biz.softfor.codegen.CodeGen;
import biz.softfor.codegen.CodeGenUtil;
import biz.softfor.jpa.crud.querygraph.ColumnDescr;
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
import java.util.Set;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import org.hibernate.type.Type;
import org.hibernate.validator.constraints.URL;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

public class FilterGen extends CodeGen {

  private final static String[] FILTER_EXCLUDED_PACKAGES
  = { Entity.class.getPackageName(), CodeGenUtil.VALIDATION_ANNOTATIONS_PKG };
  private final static String[] FILTER_FIELD_EXCLUDED_ANNOTATIONS
  = { ActionAccess.class.getName(), Type.class.getName(), URL.class.getName() };
  private final static String RESET_METHOD = "reset";

  public FilterGen(Class<?> classWithProcessingEntities) {
    super(GenFilter.class.getName(), classWithProcessingEntities);
  }

  public FilterGen() {
    super(GenFilter.class.getName(), null);
  }

  @Override
  public boolean process
  (Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    Set<? extends Element> annotatedElements
    = roundEnv.getElementsAnnotatedWith(GenFilter.class);
    for(Element e : annotatedElements) {
      String[] packages = e.getAnnotation(GenFilter.class).value();
      FilterBuilder fb = new FilterBuilder();
      for(String p : packages) {
        fb.includePackage(p);
      }
      ConfigurationBuilder cb = new ConfigurationBuilder().forPackages(packages)
      .filterInputsBy(fb).setScanners(Scanners.TypesAnnotated);
      Reflections reflections = new Reflections(cb);
      Set<Class<?>> types = reflections.getTypesAnnotatedWith(Entity.class);
      try {
        for(Class<?> t : types) {
          if(!CodeGenUtil.isWorClass(t)) {
            process(t);
          }
        }
      }
      catch(IllegalAccessException | IllegalArgumentException
      | InvocationTargetException | NoSuchFieldException | NoSuchMethodException
      | SecurityException ex) {
        processingEnv.getMessager()
        .printMessage(Diagnostic.Kind.ERROR, ex.getMessage());
      }
    }
    return false;
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
          String keyName;
          TypeName keyTypeName;
          FieldSpec.Builder keyBldr;
          resetMethod.addStatement("$N = null", fieldName);
          if(CodeGenUtil.isAnnotationPresent(dclField, OneToOne.class)) {
            fieldType = CodeGenUtil.filterTypeName(dclClass);
          } else if(CodeGenUtil.isAnnotationPresent(dclField, ManyToOne.class)) {
            fieldType = CodeGenUtil.filterTypeName(dclClass);
            Class idDeclClass = Reflection.idClass(dclClass);
            keyName = CodeGenUtil.manyToOneKeyName(dclField);
            keyTypeName = addAssignMethod
            (classBldr, TypeName.get(idDeclClass), keyName, Set.class);
            keyBldr = FieldSpec.builder(keyTypeName, keyName)
            .addModifiers(Modifier.PRIVATE);
            CodeGenUtil.addField(classBldr, keyBldr, keyTypeName, keyName, false);
            resetMethod.addStatement("$N = null", keyName);
          } else if(CodeGenUtil.isAnnotationPresent(dclField, OneToMany.class)
          || CodeGenUtil.isAnnotationPresent(dclField, ManyToMany.class)) {
            Class<?> joinClass = Reflection.genericParameter(dclField);
            fieldType = CodeGenUtil.filterTypeName(joinClass);
            Class<?> joinIdClass = Reflection.idClass(joinClass);
            keyName = ColumnDescr.toManyKeyName(fieldName);
            Class<?> collectionClass
            = CodeGenUtil.isAnnotationPresent(dclField, OneToMany.class)
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
