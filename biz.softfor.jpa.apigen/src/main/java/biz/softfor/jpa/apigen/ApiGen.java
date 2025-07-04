package biz.softfor.jpa.apigen;

import biz.softfor.codegen.CodeGen;
import biz.softfor.codegen.CodeGenUtil;
import biz.softfor.jpa.crud.querygraph.ColumnDescr;
import biz.softfor.util.Inflector;
import biz.softfor.util.Reflection;
import biz.softfor.util.StringUtil;
import biz.softfor.util.api.CommonResponse;
import biz.softfor.util.api.HaveId;
import biz.softfor.util.api.Identifiable;
import biz.softfor.util.security.ActionAccess;
import com.palantir.javapoet.AnnotationSpec;
import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.FieldSpec;
import com.palantir.javapoet.MethodSpec;
import com.palantir.javapoet.ParameterizedTypeName;
import com.palantir.javapoet.TypeName;
import com.palantir.javapoet.TypeSpec;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.type.Type;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

public class ApiGen extends CodeGen {

  private Set<Class<?>> restControllers;

  private final static String[] API_FIELD_EXCLUDED_ANNOTATIONS
  = { ActionAccess.class.getName(), Type.class.getName() };
  private final static AnnotationSpec SUPPRESS_WARNINGS
  = AnnotationSpec.builder(SuppressWarnings.class)
  .addMember(CodeGenUtil.VALUE_ANNO_PROP, "$S", "empty-statement")
  .build();
  private final static String ANNOTATION_RESTCONTROLLERS = "restControllers";

  private final static boolean DEBUG = false;

  public ApiGen() {
    super(GenApi.class);
  }

  @Override
  protected void preProcess(Element element) {
    String[] packages = new String[] {};
    AnnotationValue av = CodeGenUtil.getAnnotationProperty
    (element, supportedAnnotation, ANNOTATION_RESTCONTROLLERS);
    if(av != null) {
      List<? extends AnnotationValue> avs
      = (List<? extends AnnotationValue>)av.getValue();
      if(!avs.isEmpty()) {
        packages = new String[avs.size()];
        for(int i = 0; i < packages.length; ++i) {
          String className = avs.get(i).getValue().toString();
          packages[i] = className.substring(0, className.lastIndexOf('.'));
        }
      }
    }
    FilterBuilder fb = new FilterBuilder();
    for(String p : packages) {
      fb.includePackage(p);
    }
    ConfigurationBuilder cb = new ConfigurationBuilder().forPackages(packages)
    .filterInputsBy(fb).setScanners(Scanners.TypesAnnotated);
    restControllers
    = new Reflections(cb).getTypesAnnotatedWith(RestController.class);
  }

  @Override
  public void process(Class<?> clazz) throws IllegalAccessException
  , IllegalArgumentException, InvocationTargetException, NoSuchFieldException
  , NoSuchMethodException, SecurityException {
    final String clazzSimpleName = clazz.getSimpleName();
    final String apiPackageName = Reflection.apiPackageName(clazz.getPackageName());
    final ClassName dtoClazzName = CodeGenUtil.dtoClassName(clazz);
    final Class<?> superIdClass = Reflection.idClass(clazz);
    TypeSpec.Builder classBldr = TypeSpec.classBuilder(dtoClazzName)
    .addAnnotations(CodeGenUtil.copyAnnotations(
      clazz.getAnnotations()
    , CodeGenUtil.API_EXCLUDED_PACKAGES
    , CodeGenUtil.API_EXCLUDED_ANNOTATIONS
    , null
    ))
    .superclass(ParameterizedTypeName.get(HaveId.class, superIdClass));
    CodeGenUtil.addToString(classBldr, true);

    for(Field dclField : clazz.getDeclaredFields()) {
      int mods = dclField.getModifiers();
      String fieldName = dclField.getName();
      if(Reflection.isProperty(mods)
      && !Identifiable.ID.equals(fieldName)) {
        Class<?> dclClass = dclField.getType();
        TypeName fieldType;
        CodeGenUtil.SetterCode setterCode;
        String mappedByName = null;
        boolean unidirectional = false;
        if(CodeGenUtil.isAnnotationPresent(dclField, OneToOne.class)) {
          fieldType = CodeGenUtil.dtoClassName(dclClass);
          setterCode = CodeGenUtil.SetterCode.ONE_TO_ONE;
        } else if(CodeGenUtil.isAnnotationPresent(dclField, ManyToOne.class)) {
          fieldType = CodeGenUtil.dtoClassName(dclClass);
          setterCode = CodeGenUtil.SetterCode.SIMPLE;
          TypeName keyTypeName = TypeName.get(Reflection.idClass(dclClass));
          String keyName = CodeGenUtil.manyToOneKeyName(dclField);
          FieldSpec.Builder keyBldr = FieldSpec.builder(keyTypeName, keyName);
          CodeGenUtil.setModifiers(keyBldr, mods);
          CodeGenUtil.addField(classBldr, keyBldr, keyTypeName, keyName, true);
        } else if(CodeGenUtil.isAnnotationPresent(dclField, OneToMany.class)) {
          Class<?> dclJoinClass = Reflection.genericParameter(dclField);
          TypeName joinClass = CodeGenUtil.dtoClassName(dclJoinClass);
          fieldType = ParameterizedTypeName.get
          (ClassName.get(List.class), joinClass);
          setterCode = CodeGenUtil.SetterCode.ONE_TO_MANY;

          addFieldIds(
            ColumnDescr.toManyKeyName(fieldName)
          , classBldr
          , Reflection.idClass(dclJoinClass)
          , mods
          , fieldName
          , joinClass
          );

          mappedByName = (String)CodeGenUtil.getAnnotationProperty
          (dclField, OneToMany.class, "mappedBy");
          unidirectional = StringUtils.isEmpty(mappedByName);
          if(unidirectional) {
            mappedByName = CodeGenUtil.manyToOneKeyName(dclField);
          }
          String mappedBySetter = CodeGenUtil.setterName(mappedByName);

          MethodSpec.Builder add = MethodSpec
          .methodBuilder(CodeGenUtil.fieldMethodName("add", fieldName))
          .addModifiers(Modifier.PUBLIC)
          .addParameter(joinClass, CodeGenUtil.PARAM_NAME)
          .beginControlFlow("if($N != null)", CodeGenUtil.PARAM_NAME)
          .addStatement("$N.$N($N)", CodeGenUtil.PARAM_NAME
          , mappedBySetter, unidirectional ? "getId()" : "this")
          .beginControlFlow("if($N == null)", fieldName)
          .addStatement("$N = new $T<>()", fieldName, ArrayList.class)
          .endControlFlow()
          .addStatement("$N.add($N)", fieldName, CodeGenUtil.PARAM_NAME)
          .endControlFlow()
          ;
          classBldr.addMethod(add.build());

          MethodSpec.Builder remove = MethodSpec
          .methodBuilder(CodeGenUtil.fieldMethodName("remove", fieldName))
          .addModifiers(Modifier.PUBLIC)
          .addParameter(joinClass, CodeGenUtil.PARAM_NAME)
          .beginControlFlow("if($N != null)", CodeGenUtil.PARAM_NAME)
          .addStatement("$N.$N(null)", CodeGenUtil.PARAM_NAME, mappedBySetter)
          .beginControlFlow("if($N != null)", fieldName)
          .addStatement("$N.remove(v)", fieldName)
          .endControlFlow()
          .endControlFlow()
          ;
          classBldr.addMethod(remove.build());

          MethodSpec.Builder removeAll = MethodSpec
          .methodBuilder(CodeGenUtil.fieldMethodName("remove", fieldName))
          .addModifiers(Modifier.PUBLIC)
          .addAnnotation(SUPPRESS_WARNINGS)
          .beginControlFlow("if($N != null)", fieldName)
          .addStatement("for($T<$T> i = $N.iterator(); i.hasNext();"
          + " i.next().$N(null), i.remove());"
          , Iterator.class, joinClass, fieldName, mappedBySetter)
          .endControlFlow()
          ;
          classBldr.addMethod(removeAll.build());
        } else if(CodeGenUtil.isAnnotationPresent(dclField, ManyToMany.class)) {
          String itemName = Inflector.getInstance().singularize(fieldName);
          Class<?> dclJoinClass = Reflection.genericParameter(dclField);
          TypeName joinClass = CodeGenUtil.dtoClassName(dclJoinClass);
          fieldType
          = ParameterizedTypeName.get(ClassName.get(Set.class), joinClass);
          setterCode = CodeGenUtil.SetterCode.MANY_TO_MANY;

          addFieldIds(
            ColumnDescr.toManyKeyName(fieldName)
          , classBldr
          , Reflection.idClass(dclJoinClass)
          , mods
          , fieldName
          , joinClass
          );

          Field dclMappedByField = manyToManyMappedField(dclField);
          unidirectional = dclMappedByField == null;
          String dclMappedByName;
          String mappedByGetter;
          String mappedBySetter;
          if(unidirectional) {
            //@Todo
            dclMappedByName = null;
            mappedByGetter = null;
            mappedBySetter = null;
          } else {
            dclMappedByName = dclMappedByField.getName();
            mappedByGetter = CodeGenUtil.getterName(dclMappedByName);
            mappedBySetter = CodeGenUtil.setterName(dclMappedByName);
            mappedByName = StringUtils.uncapitalize(dclMappedByName);
          }
          if(DEBUG) {
            System.out.println(dclField + " " + "=".repeat(16));
            System.out.println("itemName=" + itemName);
            System.out.println("dclJoinClass=" + dclJoinClass);
            System.out.println("joinClass=" + joinClass);
            System.out.println("fieldTypeName=" + fieldType);
            System.out.println("keyName=" + ColumnDescr.toManyKeyName(fieldName));
            System.out.println("dclMappedByField=" + dclMappedByField);
            System.out.println("dclMappedByName=" + dclMappedByName);
            System.out.println("mappedByGetter=" + mappedByGetter);
            System.out.println("mappedBySetter=" + mappedBySetter);
            System.out.println("mappedByName=" + mappedByName);
          }
          MethodSpec.Builder addMethod = MethodSpec
          .methodBuilder(CodeGenUtil.fieldMethodName("add", itemName))
          .addParameter(joinClass, CodeGenUtil.PARAM_NAME)
          .addModifiers(Modifier.PUBLIC)
          .beginControlFlow("if($N != null)", CodeGenUtil.PARAM_NAME)
          .addStatement("$T<$T> $N = $N.$N()", Set.class, dtoClazzName
          , dclMappedByName, CodeGenUtil.PARAM_NAME, mappedByGetter)
          .beginControlFlow("if($N == null)", dclMappedByName)
          .addStatement("$N = new $T<>()", dclMappedByName, HashSet.class)
          .addStatement("$N.$N($N)"
          , CodeGenUtil.PARAM_NAME, mappedBySetter, dclMappedByName)
          .endControlFlow()
          .addStatement("$N.add(this)", dclMappedByName)
          .beginControlFlow("if($N == null)", fieldName)
          .addStatement("$N = new $T<>()", fieldName, HashSet.class)
          .endControlFlow()
          .addStatement("$N.add($N)", fieldName, CodeGenUtil.PARAM_NAME)
          ;
          addMethod.endControlFlow();
          classBldr.addMethod(addMethod.build());

          MethodSpec.Builder removeMethod = MethodSpec
          .methodBuilder(CodeGenUtil.fieldMethodName("remove", itemName))
          .addParameter(joinClass, CodeGenUtil.PARAM_NAME)
          .addModifiers(Modifier.PUBLIC)
          .beginControlFlow("if($N != null)", CodeGenUtil.PARAM_NAME)
          ;
          if(!unidirectional) {
            removeMethod.addStatement("$T<$T> $N = $N.$N()"
            , Set.class
            , dtoClazzName
            , dclMappedByName
            , CodeGenUtil.PARAM_NAME
            , mappedByGetter
            )
            .beginControlFlow("if($N != null)", dclMappedByName)
            .addStatement("$N.remove(this)", dclMappedByName)
            .endControlFlow()
            ;
          }
          removeMethod.beginControlFlow("if($N != null)", fieldName)
          .addStatement("$N.remove($N)", fieldName, CodeGenUtil.PARAM_NAME)
          .endControlFlow()
          .endControlFlow()
          ;
          classBldr.addMethod(removeMethod.build());

          MethodSpec.Builder removeAll = MethodSpec
          .methodBuilder(CodeGenUtil.fieldMethodName("remove", fieldName))
          .addModifiers(Modifier.PUBLIC)
          .addAnnotation(SUPPRESS_WARNINGS)
          .beginControlFlow("if($N != null)", fieldName)
          .beginControlFlow("for($T<$T> i = $N.iterator(); i.hasNext();)"
          , Iterator.class, joinClass, fieldName)
          .addStatement("$T<$T> $N = i.next().$N()", Set.class
          , dtoClazzName, dclMappedByName, mappedByGetter)
          .beginControlFlow("if($N != null)", dclMappedByName)
          .addStatement("$N.remove(this)", dclMappedByName)
          .endControlFlow()
          .endControlFlow()
          .endControlFlow()
          ;
          classBldr.addMethod(removeAll.build());
        } else {
          fieldType = ClassName.get(dclClass);
          setterCode = CodeGenUtil.SetterCode.SIMPLE;
        }
        FieldSpec.Builder fieldBldr = CodeGenUtil.fieldBuilder(
          dclField
        , fieldName
        , fieldType
        , CodeGenUtil.API_EXCLUDED_PACKAGES
        , API_FIELD_EXCLUDED_ANNOTATIONS
        );
        CodeGenUtil.addField(
          classBldr
        , fieldBldr
        , fieldType
        , fieldName
        , true
        , false
        , setterCode
        , mappedByName
        , unidirectional
        , null
        );
      }
    }
    CodeGenUtil.writeSrc(classBldr, apiPackageName, processingEnv);

    TypeSpec.Builder responseBldr
    = TypeSpec.classBuilder(clazzSimpleName + "Response")
    .superclass(ParameterizedTypeName.get
    (ClassName.get(CommonResponse.class), dtoClazzName));
    CodeGenUtil.writeSrc(responseBldr, apiPackageName, processingEnv);

    TypeSpec.Builder request = CodeGenUtil.newCrudRequests(
      clazzSimpleName
    , ClassName.get(superIdClass)
    , dtoClazzName
    , CodeGenUtil.filterClassName(clazz)
    );

    String ctlrName = CodeGenUtil.restControllerClassName(clazz).canonicalName();
    for(Class<?> ctlrClass : restControllers) {
      if(ctlrName.equals(ctlrClass.getName())) {
        for(Method method : ctlrClass.getMethods()) {
          Annotation requestMapping
          = CodeGenUtil.getAnnotation(method, RequestMapping.class);
          if(requestMapping != null) {
            String[] requestMappingPath = (String[])CodeGenUtil
            .getAnnotationProperty
            (method, RequestMapping.class, CodeGenUtil.PATH_ANNO_PROP);
            if(requestMappingPath == null) {
              requestMappingPath = (String[])CodeGenUtil.getAnnotationProperty
              (method, RequestMapping.class, CodeGenUtil.VALUE_ANNO_PROP);
            }
            String[] ctlrMappingPath = { "" };
            if(CodeGenUtil.getAnnotation(ctlrClass, RequestMapping.class) != null) {
              ctlrMappingPath = (String[])CodeGenUtil.getAnnotationProperty
              (ctlrClass, RequestMapping.class, CodeGenUtil.PATH_ANNO_PROP);
              if(ctlrMappingPath == null) {
                ctlrMappingPath = (String[])CodeGenUtil.getAnnotationProperty
                (ctlrClass, RequestMapping.class, CodeGenUtil.VALUE_ANNO_PROP);
              }
            }
            String ucaseMethodName
            = StringUtil.camelCaseToUnderScoreUpperCase(method.getName());
            FieldSpec.Builder pathFieldBldr = FieldSpec.builder(
              String.class, ucaseMethodName + "_PATH"
            , Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC
            )
            .initializer("$S", ctlrMappingPath[0] + requestMappingPath[0]);
            FieldSpec.Builder httpMethodFieldBldr = FieldSpec.builder(
              String.class
            , ucaseMethodName + "_METHOD"
            , Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC
            )
            .initializer(
              "$S"
            , ((Object[])CodeGenUtil.getAnnotationProperty
              (method, RequestMapping.class, CodeGenUtil.METHOD_ANNO_PROP))
              [0].toString()
            );
            request
            .addField(pathFieldBldr.build())
            .addField(httpMethodFieldBldr.build());
          }
        }
        break;
      }
    }
    CodeGenUtil.writeSrc(request, apiPackageName, processingEnv);
  }

  private static void addFieldIds(
    String fieldIdsName
  , TypeSpec.Builder dtoBldr
  , Class<?> idClass
  , int mods
  , String fieldName
  , TypeName dtoJoinClass
  ) {
    TypeName fieldIdsTypeName = ParameterizedTypeName.get(Set.class, idClass);
    FieldSpec.Builder fieldIdsBldr
    = FieldSpec.builder(fieldIdsTypeName, fieldIdsName)
    .addJavadoc("The default value of this field is {@code null}, which means "
    + "no changes when saving.");
    CodeGenUtil.setModifiers(fieldIdsBldr, mods);
    String javadoc = "This property is only used to create/update an entity. "
    + "{@code null} means no change and is the default value.";
    CodeGenUtil.addField(
      dtoBldr
    , fieldIdsBldr
    , fieldIdsTypeName
    , fieldIdsName
    , true
    , false
    , CodeGenUtil.SetterCode.SIMPLE
    , null
    , false
    , javadoc
    );
    MethodSpec.Builder getIdsMethod = MethodSpec.methodBuilder(fieldIdsName)
    .addModifiers(Modifier.PUBLIC)
    .returns(fieldIdsTypeName)
    .addStatement("$T $N", fieldIdsTypeName, CodeGenUtil.RESULT_NAME)
    .beginControlFlow("if($N == null)", fieldIdsName)
    .addStatement("$N = new $T<>(0)", CodeGenUtil.RESULT_NAME, HashSet.class)
    .nextControlFlow("else")
    .addStatement(
      "$N = new $T<>($N.size())"
    , CodeGenUtil.RESULT_NAME
    , HashSet.class
    , fieldName
    )
    .beginControlFlow("for($T e : $N)", dtoJoinClass, fieldName)
    .addStatement("$N.add(e.getId())", CodeGenUtil.RESULT_NAME)
    .endControlFlow()
    .endControlFlow()
    .addStatement("return $N", CodeGenUtil.RESULT_NAME);
    dtoBldr.addMethod(getIdsMethod.build());
  }

  @SuppressWarnings("unchecked")
  private static List<Field> annotatedFields(Class clazz, Class... annotations) {
    List<Field> result = new ArrayList<>();
    for(; clazz != null; clazz = clazz.getSuperclass()) {
      for(Field field : clazz.getDeclaredFields()) {
        for(Class a : annotations) {
          if(CodeGenUtil.isAnnotationPresent(field, a)) {
            result.add(field);
            break;
          }
        }
      }
    }
    return result;
  }

  private static Field manyToManyMappedField(Field field)
  throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    Field result = null;
    Class joinClass = Reflection.genericParameter(field);
    String mappedBy = (String)CodeGenUtil.getAnnotationProperty
    (field, ManyToMany.class, "mappedBy");
    if(StringUtils.isBlank(mappedBy)) {
      Class<?> fieldClass = field.getDeclaringClass();
      for(Field f : annotatedFields(joinClass, ManyToMany.class)) {
        mappedBy = (String)CodeGenUtil.getAnnotationProperty
        (f, ManyToMany.class, "mappedBy");
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

}
