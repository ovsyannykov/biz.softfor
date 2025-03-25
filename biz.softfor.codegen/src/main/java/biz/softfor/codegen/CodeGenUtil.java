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
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import jakarta.persistence.Entity;
import jakarta.persistence.IdClass;
import jakarta.validation.constraints.NotNull;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.tools.JavaFileObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.type.Type;

public class CodeGenUtil {

  public final static String ANNOTATION_VALUE = "value";
  public final static String PROCESSED_FIELD_NAME = "processed";
  public final static String ENTITIES_CLASS_NAME = "Entities";
  public final static String ENTITIES_FIELD_NAME = "entities";
  public final static String ENTITIES_OPTION = ENTITIES_FIELD_NAME;
  public final static String ID_GETTER_NAME = getterName(Identifiable.ID);
  public final static String ID_SETTER_NAME = setterName(Identifiable.ID);
  public final static String ID_SUFFIX = "_Id";
  public final static String PARAM_NAME = "v";
  public final static String REST_CONTROLLERS_CLASS_NAME = "RestControllers";
  public final static String REST_CONTROLLERS_FIELD_NAME = "restcontrollers";
  public final static String REST_CONTROLLERS_OPTION = REST_CONTROLLERS_FIELD_NAME;
  public final static String RESULT_NAME = "result";
  public final static Package VALIDATION_ANNOTATIONS_PKG = NotNull.class.getPackage();

  public final static Class<?>[] API_EXCLUDED_ANNOTATIONS
  = { ActionAccess.class, JsonFilter.class };
  public final static Package[] API_EXCLUDED_PACKAGES
  = { Entity.class.getPackage() };
  public final static Class<?>[] API_FIELD_EXCLUDED_ANNOTATIONS
  = { ActionAccess.class, Type.class };
  public final static Package[] FILTER_EXCLUDED_PACKAGES
  = { Entity.class.getPackage(), VALIDATION_ANNOTATIONS_PKG };
  public final static Class<?>[] FILTER_FIELD_EXCLUDED_ANNOTATIONS
  = { ActionAccess.class, Type.class };

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
        , ((ParameterizedTypeName)fieldTypeName).typeArguments.get(0))
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
        , ((ParameterizedTypeName)fieldTypeName).typeArguments.get(0))
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

  public static List<AnnotationSpec> copyAnnotations(
    Annotation[] srcAnnotations
  , Package[] excludePackages
  , Class<?>[] excludeAnnotations
  , String validatableField
  ) {
    List<AnnotationSpec> result = new ArrayList<>();
    for(Annotation a : srcAnnotations) {
      Class<?> aClass = a.annotationType().getNestHost();
      Package aPackage = aClass.getPackage();
      if(!ArrayUtils.contains(excludePackages, aPackage)
      && !ArrayUtils.contains(excludeAnnotations, aClass)) {
        AnnotationSpec as = AnnotationSpec.get(a);
        if(aPackage.equals(VALIDATION_ANNOTATIONS_PKG)) {
          as = as.toBuilder().addMember(
            "groups"
          , "{ $T.class, $L.class }"
          , Create.class
          , StringUtils.capitalize(validatableField)
          ).build();
        }
        result.add(as);
      }
    }
    return result;
  }

  public static TypeName dtoTypeName(Class<?> srcFieldClass) {
    return ClassName.get(
      Reflection.apiPackageName(srcFieldClass.getPackageName())
    , Reflection.dtoClassName(srcFieldClass.getSimpleName())
    );
  }

  public static FieldSpec.Builder fieldBuilder(
    Field dclField
  , String fieldName
  , TypeName fieldType
  , Package[] excludePackages
  , Class<?>[] excludeAnnotations
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

  public static TypeName filterTypeName(Class<?> srcFieldClass) {
    return ClassName.get(
      Reflection.apiPackageName(srcFieldClass.getPackageName())
    , Reflection.filterClassName(srcFieldClass.getSimpleName())
    );
  }

  public static AnnotationSpec generated(String srcClassName) {
    return AnnotationSpec.builder(Generated.class)
    .addMember(ANNOTATION_VALUE, "$S", srcClassName)
    .build();
  }

  private static String srcPath;
  private final static String SAMPLE = "sample_";

  synchronized public static
  String generatedSourcesPath(ProcessingEnvironment processingEnv) {
    if(srcPath == null) {
      try {
        String samplePath = processingEnv.getFiler().createSourceFile(SAMPLE).getName();
        srcPath = samplePath.substring(0, samplePath.indexOf(SAMPLE) - 1);
      }
      catch(IOException ex) {
        throw new RuntimeException("Can't get access to generated sources path.", ex);
      }
    }
    return srcPath;
  }

  public static String getterName(String fieldName) {
    return fieldMethodName("get", fieldName);
  }

  public static String setterName(String fieldName) {
    return fieldMethodName("set", fieldName);
  }

  public static boolean isLinkClass(Class<?> clazz) {
    IdClass idClass = clazz.getAnnotation(IdClass.class);
    return idClass != null && idClass.value().getSimpleName().endsWith(ID_SUFFIX);
  }

  public static TypeSpec.Builder newCrudRequests
  (String entityName, TypeName idCN, TypeName dataCN, TypeName filterCN) {
    TypeSpec.Builder result = TypeSpec.classBuilder(entityName + "Request");
    request(result, "Create"
    , reqBldr -> addConstructors4Create.accept(reqBldr, dataCN)
    , CreateRequest.class, idCN, dataCN);
    request(result, "Read", noConstructors, ReadRequest.class, idCN, filterCN);
    request(result, "Update", reqBldr -> {
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
    request(result, "Delete", noConstructors, DeleteRequest.class
    , idCN, filterCN);
    return result;
  }

  public static TypeSpec.Builder newCrudRequests
  (String requestName, TypeName[][] genericClassParameters) {
    TypeSpec.Builder result = TypeSpec.classBuilder(requestName);
    String[] names = new String[] { "Create", "Read", "Update", "Delete" };
    TypeSpec.Builder[] requests = new TypeSpec.Builder[4];
    for(int i = 0; i < names.length; ++i) {
      TypeName[] genericParameters = new TypeName[genericClassParameters[i].length - 1];
      for(int p = 1; p < genericClassParameters[i].length; ++p) {
        genericParameters[p - 1] = genericClassParameters[i][p];
      }
      requests[i] = TypeSpec.classBuilder(names[i])
      .superclass(ParameterizedTypeName.get((ClassName)genericClassParameters[i][0], genericParameters))
      .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
      if(i == 0 || i == 2) {//Create or Update
        MethodSpec.Builder ctor
        = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC);
        requests[i].addMethod(ctor.build());
        MethodSpec.Builder dataCtor
        = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)
        .addParameter(genericParameters[1], AbstractRequest.DATA)
        .addStatement("super($N)", AbstractRequest.DATA);
        requests[i].addMethod(dataCtor.build());
        if(i == 2) {
          MethodSpec.Builder updateCtor
          = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)
          .addParameter(genericParameters[1], AbstractRequest.DATA)
          .addParameter(
            ParameterizedTypeName.get(List.class, String.class)
          , AbstractRequest.FIELDS
          )
          .addStatement
          ("super($N, $N)", AbstractRequest.DATA, AbstractRequest.FIELDS);
          requests[i].addMethod(updateCtor.build());
        }
      }
      addToStringAndEqualsAndHashCode(requests[i], true);
      result.addType(requests[i].build());
    }
    return result;
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

  public static void writeSrc(
    TypeSpec.Builder bldr
  , String packageName
  , ProcessingEnvironment processingEnv
  ) {
    TypeSpec classSpec = bldr.addModifiers(Modifier.PUBLIC).build();
    String fileName = packageName + "." + classSpec.name;
    if(!created.contains(fileName)) {
      created.add(fileName);
      try {
        JavaFileObject fileObject = processingEnv.getFiler().createSourceFile(fileName);
        try( Writer writer = fileObject.openWriter()) {
          try( Writer out = new BufferedWriter(writer)) {
            JavaFile.builder(packageName, classSpec)
            .addFileComment("Automatically generated. Don't modify!")
            .build().writeTo(out);
          }
          catch(IOException ex) {
            throw new RuntimeException(ex);
          }
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

  private final static Set<String> created = new HashSet();

  private final static Consumer<TypeSpec.Builder>
  noConstructors = reqBldr -> {};

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

}
