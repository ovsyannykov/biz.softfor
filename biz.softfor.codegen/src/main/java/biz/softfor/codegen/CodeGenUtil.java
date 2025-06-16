package biz.softfor.codegen;

import biz.softfor.util.Create;
import biz.softfor.util.Generated;
import biz.softfor.util.Inflector;
import biz.softfor.util.Reflection;
import biz.softfor.util.StringUtil;
import biz.softfor.util.api.AbstractRequest;
import biz.softfor.util.api.CreateRequest;
import biz.softfor.util.api.DeleteRequest;
import biz.softfor.util.api.Identifiable;
import biz.softfor.util.api.ReadRequest;
import biz.softfor.util.api.UpdateRequest;
import biz.softfor.util.security.ActionAccess;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.palantir.javapoet.AnnotationSpec;
import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.FieldSpec;
import com.palantir.javapoet.JavaFile;
import com.palantir.javapoet.MethodSpec;
import com.palantir.javapoet.ParameterizedTypeName;
import com.palantir.javapoet.TypeName;
import com.palantir.javapoet.TypeSpec;
import jakarta.persistence.Entity;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.NotNull;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.tools.JavaFileObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class CodeGenUtil {

  public final static String EXCLUDE_ANNO_PROP = "exclude";
  public final static String METHOD_ANNO_PROP = "method";
  public final static String PATH_ANNO_PROP = "path";
  public final static String VALUE_ANNO_PROP = "value";

  public final static String ID_GETTER_NAME = getterName(Identifiable.ID);
  public final static String ID_SETTER_NAME = setterName(Identifiable.ID);
  public final static String ID_SUFFIX = "_Id";
  public final static String PARAM_NAME = "v";
  public final static String RESULT_NAME = "result";
  public final static String VALIDATION_ANNOTATIONS_PKG
  = NotNull.class.getPackageName();
  public final static String[] API_EXCLUDED_ANNOTATIONS
  = { ActionAccess.class.getName(), JsonFilter.class.getName() };
  public final static String[] API_EXCLUDED_PACKAGES
  = { Entity.class.getPackageName() };
  public final static String SERVICE_PART_PACKAGE_NAME = ".spring";
  public final static String SERVICE_SFX = "Svc";
  private final static String RESTCONTROLLER_PART_PACKAGE_NAME = ".spring.rest";
  public final static String RESTCONTROLLER_SFX = "Ctlr";

  private final static Set<String> created = new HashSet<>();
  private final static Consumer<TypeSpec.Builder> noConstructors = reqBldr -> {};
  private final static BiConsumer<TypeSpec.Builder, TypeName>
  addConstructors4Create = (reqBldr, dataCN) -> {
    MethodSpec.Builder ctor
    = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC);
    reqBldr.addMethod(ctor.build());
    MethodSpec.Builder dataCtor
    = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)
    .addParameter(dataCN, AbstractRequest.DATA)
    .addStatement("super($N)", AbstractRequest.DATA);
    reqBldr.addMethod(dataCtor.build());
  };

  public static void addField(
    TypeSpec.Builder classBldr
  , FieldSpec.Builder fieldBldr
  , TypeName type
  , String name
  , boolean validatable
  , boolean isOverridden
  , SetterCode code
  , String joinName
  , boolean unidirectional
  , String javadoc
  ) {
    addFieldNameConstant(classBldr, name);
    classBldr.addField(fieldBldr.build());
    addGetter(classBldr, type, name, isOverridden, javadoc);
    addSetter(
      classBldr
    , type
    , name
    , isOverridden
    , code
    , joinName
    , unidirectional
    , javadoc
    );
    if(validatable) {
      TypeSpec validationGroup
      = TypeSpec.interfaceBuilder(StringUtils.capitalize(name))
      .addModifiers(Modifier.PUBLIC).build();
      classBldr.addType(validationGroup);
    }
  }

  public static void addField(
    TypeSpec.Builder classBldr
  , FieldSpec.Builder fieldBldr
  , TypeName type
  , String name
  , boolean validatable
  ) {
    addField(
      classBldr
    , fieldBldr
    , type
    , name
    , validatable
    , false
    , SetterCode.SIMPLE
    , null
    , false
    , null
    );
  }

  public static void addFieldNameConstant(
    TypeSpec.Builder classBldr, String fieldName
  ) {
    classBldr.addField(
      FieldSpec.builder(
        String.class
      , StringUtil.camelCaseToUnderScoreUpperCase(fieldName)
      , Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC
      )
      .initializer("$S", fieldName)
      .build()
    );
  }

  public static void addGetter(
    TypeSpec.Builder classBldr
  , String methodName
  , TypeName fieldTypeName
  , String fieldName
  , boolean isOverridden
  , String javadoc
  ) {
    MethodSpec.Builder method = MethodSpec.methodBuilder(methodName)
    .addStatement("return $N", fieldName)
    .returns(fieldTypeName)
    .addModifiers(Modifier.PUBLIC);
    if(isOverridden) {
      method.addAnnotation(Override.class);
    }
    if(StringUtils.isNotBlank(javadoc)) {
      method.addJavadoc(javadoc);
    }
    classBldr.addMethod(method.build());
  }

  public static void addGetter(
    TypeSpec.Builder classBldr
  , TypeName fieldTypeName
  , String fieldName
  , boolean isOverridden
  , String javadoc
  ) {
    addGetter(
      classBldr
    , getterName(fieldName)
    , fieldTypeName
    , fieldName
    , isOverridden
    , javadoc
    );
  }

  public static enum SetterCode {

    SIMPLE {

      @Override
      public void add(
        MethodSpec.Builder method
      , TypeName fieldTypeName
      , String fieldName
      , String joinName
      , boolean unidirectional
      ) {
        method.addStatement("$N = v", fieldName);
      }

    }, ONE_TO_ONE() {

      @Override
      public void add(
        MethodSpec.Builder method
      , TypeName fieldTypeName
      , String fieldName
      , String joinName
      , boolean unidirectional
      ) {
        method
        .beginControlFlow("if(v == null)")
        .beginControlFlow("if($N != null)", fieldName)
        .addStatement("$N.$N(null)", fieldName, ID_SETTER_NAME)
        .endControlFlow()
        .nextControlFlow("else")
        .addStatement("v.$N($N())", ID_SETTER_NAME, ID_GETTER_NAME)
        .endControlFlow()
        .addStatement("$N = v", fieldName);
      }

    }, ONE_TO_MANY() {

      @Override
      public void add(
        MethodSpec.Builder method
      , TypeName fieldTypeName
      , String fieldName
      , String joinName
      , boolean unidirectional
      ) {
        method
        .beginControlFlow("if($N != v)", fieldName)
        .addStatement("$N()", "remove" + StringUtils.capitalize(fieldName))
        .beginControlFlow("if(v != null)")
        .beginControlFlow("for($T e : v)"
        , ((ParameterizedTypeName)fieldTypeName).typeArguments().get(0))
        .addStatement("e.$N($N)"
        , setterName(joinName), unidirectional ? ID_GETTER_NAME + "()" : "this")
        .endControlFlow()
        .endControlFlow()
        .addStatement("$N = v", fieldName)
        .endControlFlow();
      }

    }, MANY_TO_MANY() {

      @Override
      public void add(
        MethodSpec.Builder method
      , TypeName fieldTypeName
      , String fieldName
      , String mappedByName
      , boolean unidirectional
      ) {
        method.beginControlFlow("if($N != v)", fieldName)
        .addStatement("$N()", fieldMethodName("remove", fieldName))
        .beginControlFlow("if(v != null)")
        .beginControlFlow("for($T e : v)"
        , ((ParameterizedTypeName)fieldTypeName).typeArguments().get(0))
        .addStatement("e.$N(this)"
        , fieldMethodName("add", Inflector.getInstance().singularize(mappedByName)))
        .endControlFlow()
        .endControlFlow()
        .addStatement("$N = v", fieldName)
        .endControlFlow()
        ;
      }

    };

    public abstract void add(
      MethodSpec.Builder method
    , TypeName fieldTypeName
    , String fieldName
    , String joinName
    , boolean unidirectional
    );

  }

  public static void addSetter(
    TypeSpec.Builder classBldr
  , TypeName fieldTypeName
  , String fieldName
  , boolean isOverridden
  , SetterCode code
  , String joinName
  , boolean unidirectional
  , String javadoc
  ) {
    MethodSpec.Builder method = MethodSpec.methodBuilder(setterName(fieldName))
    .addModifiers(Modifier.PUBLIC)
    .addParameter(fieldTypeName, "v");
    if(isOverridden) {
      method.addAnnotation(Override.class);
    }
    code.add(method, fieldTypeName, fieldName, joinName, unidirectional);
    if(StringUtils.isNotBlank(javadoc)) {
      method.addJavadoc(javadoc);
    }
    classBldr.addMethod(method.build());
  }

  public static void addToString
  (TypeSpec.Builder classBldr, boolean isCallSuper) {
    AnnotationSpec.Builder toString = AnnotationSpec.builder(ToString.class);
    if(isCallSuper) {
      toString.addMember("callSuper", "true");
    }
    classBldr.addAnnotation(toString.build());
  }

  public static void addToStringAndEqualsAndHashCode
  (TypeSpec.Builder classBldr, boolean isCallSuper) {
    addToString(classBldr, isCallSuper);
    AnnotationSpec.Builder equalsAndHashCode
    = AnnotationSpec.builder(EqualsAndHashCode.class);
    if(isCallSuper) {
      equalsAndHashCode.addMember("callSuper", "true");
    }
    classBldr.addAnnotation(equalsAndHashCode.build());
  }

  public static ClassName restControllerClassName(Class<?> clazz) {
    return ClassName.get(
      clazz.getPackageName().replace
      (Reflection.JPA_PART_PACKAGE_NAME, RESTCONTROLLER_PART_PACKAGE_NAME)
    , clazz.getSimpleName() + RESTCONTROLLER_SFX
    );
  }

  public static List<AnnotationSpec> copyAnnotations(
    Annotation[] srcAnnotations
  , String[] excludePackages
  , String[] excludeAnnotations
  , String validatedField
  ) {
    List<AnnotationSpec> result = new ArrayList<>();
    for(Annotation a : srcAnnotations) {
      Class<?> aClass = a.annotationType().getNestHost();
      String aPackageName = aClass.getPackageName();
      if(!ArrayUtils.contains(excludePackages, aPackageName)
      && !ArrayUtils.contains(excludeAnnotations, aClass.getName())) {
        AnnotationSpec as = AnnotationSpec.get(a);
        if(VALIDATION_ANNOTATIONS_PKG.equals(aPackageName)) {
          as = as.toBuilder().addMember(
            "groups"
          , "{ $T.class, $L.class }"
          , Create.class
          , StringUtils.capitalize(validatedField)
          ).build();
        }
        result.add(as);
      }
    }
    return result;
  }

  public static ClassName dtoClassName(Class<?> clazz) {
    return ClassName.get(
      Reflection.apiPackageName(clazz.getPackageName())
    , Reflection.dtoClassName(clazz.getSimpleName())
    );
  }

  public static FieldSpec.Builder fieldBuilder(
    Field dclField
  , String fieldName
  , TypeName fieldType
  , String[] excludePackages
  , String[] excludeAnnotations
  ) {
    FieldSpec.Builder result = FieldSpec.builder(fieldType, fieldName)
    .addAnnotations(copyAnnotations(
      dclField.getAnnotations()
    , excludePackages
    , excludeAnnotations
    , fieldName
    ));
    setModifiers(result, dclField.getModifiers());
    return result;
  }

  public static String fieldMethodName(String methodName, String fieldName) {
    return methodName + StringUtils.capitalize(fieldName);
  }

  public static ClassName filterClassName(Class<?> clazz) {
    return ClassName.get(
      Reflection.apiPackageName(clazz.getPackageName())
    , Reflection.filterClassName(clazz.getSimpleName())
    );
  }

  public static AnnotationSpec generated(String srcClassName) {
    return AnnotationSpec.builder(Generated.class)
    .addMember(VALUE_ANNO_PROP, "$S", srcClassName)
    .build();
  }

  public static Annotation getAnnotation
  (AnnotatedElement element, Class<? extends Annotation> annotationClass) {
    Annotation result = null;
    String name = annotationClass.getName();
    for(Annotation a : element.getAnnotations()) {
      if(name.equals(a.annotationType().getName())) {
        result = a;
        break;
      }
    }
    return result;
  }

  public static Object getAnnotationProperty
  (AnnotatedElement element, Class<? extends Annotation> clazz, String property)
  throws IllegalAccessException, InvocationTargetException
  , NoSuchMethodException, SecurityException {
    Annotation a = getAnnotation(element, clazz);
    return a == null ? null : a.getClass().getMethod(property).invoke(a);
  }

  public static AnnotationMirror getAnnotation
  (Element element, Class<? extends Annotation> clazz) {
    AnnotationMirror result = null;
    String clazzName = clazz.getName();
    for(AnnotationMirror a : element.getAnnotationMirrors()) {
      if(a.getAnnotationType().toString().equals(clazzName)) {
        result = a;
      }
    }
    return result;
  }

  public static AnnotationValue getAnnotationProperty
  (Element element, Class<? extends Annotation> clazz, String property) {
    AnnotationValue result = null;
    AnnotationMirror a = getAnnotation(element, clazz);
    if(a != null) {
      for(Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry
      : a.getElementValues().entrySet()) {
        if(entry.getKey().getSimpleName().toString().equals(property)) {
          result = entry.getValue();
        }
      }
    }
    return result;
  }

  public static String getterName(String fieldName) {
    return fieldMethodName("get", fieldName);
  }

  public static String setterName(String fieldName) {
    return fieldMethodName("set", fieldName);
  }

  public static boolean isAnnotationPresent
  (AnnotatedElement element, Class<?> anno) {
    boolean result = false;
    String annoName = anno.getName();
    for(Annotation a : element.getAnnotations()) {
      if(annoName.equals(a.annotationType().getName())) {
        result = true;
        break;
      }
    }
    return result;
  }

  public static boolean isLinkClass(Class<?> clazz)
  throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    Class<?> v
    = (Class<?>)getAnnotationProperty(clazz, IdClass.class, VALUE_ANNO_PROP);
    return v != null && v.getSimpleName().endsWith(ID_SUFFIX);
  }

  public static String manyToOneKeyName(Field field)
  throws IllegalAccessException, InvocationTargetException
  , NoSuchMethodException, SecurityException {
    return (String)getAnnotationProperty(field, JoinColumn.class, "name");
  }

  public static TypeSpec.Builder newCrudRequests
  (String entityName, TypeName idCN, TypeName dataCN, TypeName filterCN) {
    TypeSpec.Builder result
    = TypeSpec.classBuilder(entityName + Reflection.REQUEST);
    request(result, Reflection.CREATE
    , reqBldr -> addConstructors4Create.accept(reqBldr, dataCN)
    , CreateRequest.class, idCN, dataCN);
    request
    (result, Reflection.READ, noConstructors, ReadRequest.class, idCN, filterCN);
    request(result, Reflection.UPDATE, reqBldr -> {
      addConstructors4Create.accept(reqBldr, dataCN);
      MethodSpec.Builder ctor
      = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)
      .addParameter(dataCN, AbstractRequest.DATA)
      .addParameter(
        ParameterizedTypeName.get(List.class, String.class)
      , AbstractRequest.FIELDS
      )
      .addStatement("super($N, $N)", AbstractRequest.DATA, AbstractRequest.FIELDS);
      reqBldr.addMethod(ctor.build());
    }, UpdateRequest.class, idCN, filterCN, dataCN);
    request
    (result, Reflection.DELETE, noConstructors, DeleteRequest.class, idCN, filterCN);
    return result;
  }

  private static void request(
    TypeSpec.Builder bldr
  , String name
  , Consumer<TypeSpec.Builder> addConstructors
  , Class<?> superClass
  , TypeName... params
  ) {
    ClassName superCN = ClassName.get(superClass);
    TypeSpec.Builder reqBldr = TypeSpec.classBuilder(name)
    .superclass(ParameterizedTypeName.get(superCN, params))
    .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
    addToStringAndEqualsAndHashCode(reqBldr, true);
    addConstructors.accept(reqBldr);
    bldr.addType(reqBldr.build());
  }

  public static void setModifiers(FieldSpec.Builder fieldBldr, int modifiers) {
    if(java.lang.reflect.Modifier.isPublic(modifiers)) {
      fieldBldr.addModifiers(Modifier.PUBLIC);
    }
    if(java.lang.reflect.Modifier.isProtected(modifiers)) {
      fieldBldr.addModifiers(Modifier.PROTECTED);
    }
    if(java.lang.reflect.Modifier.isPrivate(modifiers)) {
      fieldBldr.addModifiers(Modifier.PRIVATE);
    }
    if(java.lang.reflect.Modifier.isTransient(modifiers)) {
      fieldBldr.addModifiers(Modifier.TRANSIENT);
    }
    if(java.lang.reflect.Modifier.isVolatile(modifiers)) {
      fieldBldr.addModifiers(Modifier.VOLATILE);
    }
  }

  public static ClassName svcClassName(Class<?> clazz) {
    return ClassName.get(
      clazz.getPackageName().replace
      (Reflection.JPA_PART_PACKAGE_NAME, SERVICE_PART_PACKAGE_NAME)
    , clazz.getSimpleName() + CodeGenUtil.SERVICE_SFX
    );
  }

  public static ClassName worClassName(Class<?> clazz) {
    return ClassName.get
    (clazz.getPackageName(), Reflection.worClassName(clazz.getSimpleName()));
  }

  public static void writeSrc(
    TypeSpec.Builder bldr
  , String packageName
  , ProcessingEnvironment processingEnv
  ) {
    TypeSpec classSpec = bldr.addModifiers(Modifier.PUBLIC).build();
    String fileName = packageName + "." + classSpec.name();
    if(!created.contains(fileName)) {
      created.add(fileName);
      try {
        JavaFileObject fileObject = processingEnv.getFiler().createSourceFile(fileName);
        try(Writer writer = fileObject.openWriter();
        Writer out = new BufferedWriter(writer)) {
          JavaFile.builder(packageName, classSpec)
          .addFileComment("Automatically generated. Don't modify!").build()
          .writeTo(out);
        }
        catch(IOException ex) {
          throw new RuntimeException(ex);
        }
      }
      catch(IOException ex) {
        throw new RuntimeException(ex);
      }
    }
  }

}
