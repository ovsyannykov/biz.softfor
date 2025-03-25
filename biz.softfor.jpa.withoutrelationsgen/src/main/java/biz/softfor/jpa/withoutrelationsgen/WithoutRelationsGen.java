package biz.softfor.jpa.withoutrelationsgen;

import biz.softfor.codegen.CodeGen;
import biz.softfor.codegen.CodeGenUtil;
import biz.softfor.codegen.CodeGenUtil.SetterCode;
import biz.softfor.jpa.crud.querygraph.ColumnDescr;
import biz.softfor.jpa.crud.querygraph.ManyToManyGeneratedLink;
import biz.softfor.jpa.crud.querygraph.ManyToManyInf;
import biz.softfor.util.Reflection;
import biz.softfor.util.StringUtil;
import biz.softfor.util.api.Identifiable;
import biz.softfor.util.security.ActionAccess;
import biz.softfor.util.security.ClassRoleCalc;
import biz.softfor.util.security.DefaultAccess;
import biz.softfor.util.security.FieldRoleCalc;
import biz.softfor.util.security.RoleCalc;
import biz.softfor.util.security.UpdateAccess;
import biz.softfor.util.security.UpdateFieldRoleCalc;
import biz.softfor.util.security.UpdateRoleCalc;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.AssertTrue;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.tools.Diagnostic;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Fetch;

public class WithoutRelationsGen extends CodeGen {

  private final static Class<?>[] WOR_EXCLUDED_ANNOTATIONS
  = { ActionAccess.class, JsonFilter.class };
  private final static Package[] WOR_FIELD_EXCLUDED_PACKAGES
  = { Column.class.getPackage() };
  private final static Class<?>[] WOR_FIELD_EXCLUDED_ANNOTATIONS
  = { ActionAccess.class, Column.class, Fetch.class };
  private final static String SERIAL_VERSION_UID = "serialVersionUID";

  private final static boolean DEBUG = false;

  protected WithoutRelationsGen(Class<?> classWithProcessingEntities) {
    super(GenWithoutRelations.class.getName(), classWithProcessingEntities);
  }

  @Override
  public void process(Class<?> clazz) throws IllegalAccessException
  , IllegalArgumentException, NoSuchFieldException, SecurityException {
    Class<?> idClass = Reflection.idClass(clazz);
    TypeName idTypeName = TypeName.get(idClass);
    String clazzPackageName = clazz.getPackageName();
    String clazzSimpleName = clazz.getSimpleName();
    String worSimpleName = Reflection.worClassName(clazzSimpleName);
    ClassName worClazzName = ClassName.get(clazzPackageName, worSimpleName);
    if(DEBUG) {
      System.out.println(
        "clazzSimpleName=" + clazzSimpleName
      + ", worSimpleName=" + worSimpleName
      + ", idClass=" + idClass.getSimpleName()
      );
    }

    TypeSpec.Builder classBldr = TypeSpec.classBuilder(worSimpleName)
    .addAnnotation(CodeGenUtil.generated(clazz.getName()))
    .addAnnotations(CodeGenUtil.copyAnnotations
    (clazz.getAnnotations(), null, WOR_EXCLUDED_ANNOTATIONS, null));
    ActionAccess aa = clazz.getAnnotation(ActionAccess.class);
    if(aa != null) {
      long aaId = new ClassRoleCalc(clazz).id();
      AnnotationSpec.Builder aaBldr = AnnotationSpec.builder(ActionAccess.class)
      .addMember(CodeGenUtil.ANNOTATION_VALUE, "$S", aa.value())
      .addMember(ActionAccess.DESCRIPTION, "$S", aa.description())
      .addMember(ActionAccess.ID, "$LL", aaId)
      .addMember(ActionAccess.DEFAULT_ACCESS, "$T.$L", DefaultAccess.class
      , aa.defaultAccess().name())
      ;
      classBldr.addAnnotation(aaBldr.build());
    }
    Class<?> superClass = clazz.getSuperclass();
    boolean isIdentifiable = Object.class.equals(superClass);
    MethodSpec.Builder ctor1 = MethodSpec.constructorBuilder()
    .addModifiers(Modifier.PUBLIC).addParameter(clazz, CodeGenUtil.PARAM_NAME);
    MethodSpec.Builder ctor2 = MethodSpec.constructorBuilder()
    .addModifiers(Modifier.PUBLIC)
    .addParameter(worClazzName, CodeGenUtil.PARAM_NAME);
    if(isIdentifiable) {
      CodeGenUtil.addToString(classBldr, false);
      MethodSpec.Builder equalsBldr
      = MethodSpec.methodBuilder(Reflection.EQUALS_METHOD)
      .addAnnotation(Override.class)
      .addAnnotation(
        AnnotationSpec.builder(SuppressWarnings.class)
        .addMember(
          CodeGenUtil.ANNOTATION_VALUE
        , "$S"
        , "EqualsWhichDoesntCheckParameterClass"
        )
        .build()
      )
      .addModifiers(Modifier.PUBLIC)
      .returns(boolean.class)
      .addParameter(Object.class, "a")
      .addStatement
      ("return $T.$N(this, a)", Identifiable.class, Reflection.EQUALS_METHOD)
      ;
      classBldr.addMethod(equalsBldr.build());
      MethodSpec.Builder hashCodeBldr
      = MethodSpec.methodBuilder(Reflection.HASHCODE_METHOD)
      .addAnnotation(Override.class)
      .addModifiers(Modifier.PUBLIC)
      .returns(int.class)
      .addStatement(
        "return $T.$N($L)"
      , Objects.class
      , Reflection.HASHCODE_METHOD
      , Identifiable.ID
      )
      ;
      classBldr.addMethod(hashCodeBldr.build());
      classBldr.addSuperinterface
      (ParameterizedTypeName.get(Identifiable.class, idClass));
    } else {
      classBldr.superclass
      (ParameterizedTypeName.get(superClass, idClass));
      ctor1.addStatement("super($N)", CodeGenUtil.PARAM_NAME);
      ctor2.addStatement("super($N)", CodeGenUtil.PARAM_NAME);
    }
    addSerializable(classBldr, processingEnv, clazz);
    if(clazz.isAnnotationPresent(JsonFilter.class)) {
      classBldr.addAnnotation(AnnotationSpec.builder(JsonFilter.class)
      .addMember(CodeGenUtil.ANNOTATION_VALUE, "$S", worSimpleName).build());
    }
    classBldr.addMethod
    (MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC).build());
    ctor1.beginControlFlow("if($N != null)", CodeGenUtil.PARAM_NAME);
    ctor2.beginControlFlow("if($N != null)", CodeGenUtil.PARAM_NAME);

    MethodSpec.Builder idSetter = null;
    for(Field dclField : clazz.getDeclaredFields()) {
      int mods = dclField.getModifiers();
      if(Reflection.isProperty(mods)) {
        SetterCode setterCode = SetterCode.SIMPLE;
        String joinName = null;
        String dclName = dclField.getName();
        Class dclClass = dclField.getType();
        String fieldName;
        TypeName fieldType;
        FieldSpec.Builder fieldBldr;
        if(dclField.isAnnotationPresent(OneToOne.class)) {
          setterCode = SetterCode.ONE_TO_ONE;
          fieldName = dclName;
          fieldType = ClassName.get(
            dclClass.getPackageName()
          , Reflection.worClassName(dclClass.getSimpleName())
          );
          fieldBldr = CodeGenUtil.fieldBuilder(
            dclField
          , fieldName
          , fieldType
          , new Package[] {
              Column.class.getPackage()
            , ToString.class.getPackage()
            , JsonIgnoreProperties.class.getPackage()
            }
          , null
          );
          fieldBldr.addAnnotation(Transient.class);
          if(idSetter == null) {
            idSetter = MethodSpec.methodBuilder(CodeGenUtil.ID_SETTER_NAME)
            .addParameter(idClass, Identifiable.ID)
            .addStatement("super.$N($N)"
            , CodeGenUtil.ID_SETTER_NAME, Identifiable.ID)
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override.class);
          }
          idSetter
          .beginControlFlow("if($N != null)", fieldName)
          .addStatement("$N.$N($N)"
          , fieldName, CodeGenUtil.ID_SETTER_NAME, Identifiable.ID)
          .endControlFlow();
          ctor1.addStatement("$N = new $T($N.$N())"
          , fieldName
          , fieldType
          , CodeGenUtil.PARAM_NAME
          , CodeGenUtil.getterName(dclName)
          );
          ctor2.addStatement("$N = new $T($N.$N())"
          , fieldName
          , fieldType
          , CodeGenUtil.PARAM_NAME
          , CodeGenUtil.getterName(dclName)
          );
        } else if(dclField.isAnnotationPresent(ManyToOne.class)) {
          fieldName = ColumnDescr.manyToOneKeyName(dclField);
          fieldType = ClassName.get(Reflection.idClass(dclClass));
          fieldBldr = CodeGenUtil.fieldBuilder(
            dclField
          , fieldName
          , fieldType
          , new Package[] {
              Column.class.getPackage()
            , ToString.class.getPackage()
            , JsonIgnoreProperties.class.getPackage()
            }
          , null
          );
          fieldBldr.addAnnotation(Column.class);
          ctor1.addStatement("$N = $T.id($N.$N())"
          , fieldName
          , ClassName.get(Identifiable.class)
          , CodeGenUtil.PARAM_NAME
          , CodeGenUtil.getterName(dclName)
          );
        } else if(dclField.isAnnotationPresent(OneToMany.class)) {
          fieldName = ColumnDescr.toManyKeyName(dclName);
          Class<?> joinClass = Reflection.genericParameter(dclField);
          Class<?> joinIdClass = Reflection.idClass(joinClass);
          fieldType = ParameterizedTypeName.get(List.class, joinIdClass);
          fieldBldr = CodeGenUtil.fieldBuilder(
            dclField
          , fieldName
          , fieldType
          , new Package[] {
              Column.class.getPackage()
            , ToString.class.getPackage()
            , JsonIgnoreProperties.class.getPackage()
            }
          , new Class<?>[] { Fetch.class }
          );
          fieldBldr.addAnnotation(Transient.class);
          ctor1.addStatement("$N = $T.idList($N.$N())"
          , fieldName
          , ClassName.get(Identifiable.class)
          , CodeGenUtil.PARAM_NAME
          , CodeGenUtil.getterName(dclName)
          );
        } else if(dclField.isAnnotationPresent(ManyToMany.class)) {
          ManyToManyInf m2mInf = new ManyToManyInf(dclField);
          Class<?> joinClass = Reflection.genericParameter(dclField);
          Class<?> joinIdClass = Reflection.idClass(joinClass);
          fieldName = m2mInf.inverseJoinColumn + "s";
          fieldType = ParameterizedTypeName.get(Set.class, joinIdClass);
          fieldBldr = CodeGenUtil.fieldBuilder(
            dclField
          , fieldName
          , fieldType
          , new Package[] {
              Column.class.getPackage()
            , ToString.class.getPackage()
            , JsonIgnoreProperties.class.getPackage()
            }
          , null
          );
          fieldBldr.addAnnotation(Transient.class);
          ctor1.addStatement("$N = $T.ids($N.$N())"
          , fieldName
          , ClassName.get(Identifiable.class)
          , CodeGenUtil.PARAM_NAME
          , CodeGenUtil.getterName(dclName)
          );

          TypeName linksTypeName = ParameterizedTypeName.get(
            ClassName.get(Set.class)
          , ClassName.get(m2mInf.packageName, m2mInf.className)
          );
          AnnotationSpec.Builder oneToMany
          = AnnotationSpec.builder(OneToMany.class)
          .addMember("fetch", "$T.$L", FetchType.class, FetchType.LAZY.name());
          AnnotationSpec.Builder joinColumn
          = AnnotationSpec.builder(JoinColumn.class)
          .addMember("name", "$S", m2mInf.joinColumn)
          .addMember("insertable", "$L", false)
          .addMember("updatable", "$L", false);
          FieldSpec.Builder linksBldr = FieldSpec
          .builder(linksTypeName, m2mInf.fieldName, Modifier.PRIVATE)
          .addAnnotation(oneToMany.build())
          .addAnnotation(joinColumn.build());
          checkAndAddActionAccess(linksBldr, clazz, dclField);
          classBldr.addField(linksBldr.build());
          CodeGenUtil.addGetter
          (classBldr, linksTypeName, m2mInf.fieldName, false, null);

          MethodSpec.Builder allArgsCtor
          = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)
          .addParameter(idClass, m2mInf.joinColumn)
          .addParameter(joinIdClass, m2mInf.inverseJoinColumn)
          .addStatement("this.$N = $N"
          , m2mInf.joinColumn, m2mInf.joinColumn)
          .addStatement("this.$N = $N"
          , m2mInf.inverseJoinColumn, m2mInf.inverseJoinColumn);
          MethodSpec.Builder ctor
          = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC);

          String linkIdClassName = m2mInf.className + CodeGenUtil.ID_SUFFIX;
          TypeSpec.Builder linkIdClassBldr
          = TypeSpec.classBuilder(linkIdClassName)
          .addAnnotation(EqualsAndHashCode.class)
          .addAnnotation(ToString.class)
          .addMethod(allArgsCtor.build())
          .addMethod(ctor.build());
          FieldSpec.Builder linkIdJoinBldr
          = FieldSpec.builder(idTypeName, m2mInf.joinColumn, Modifier.PRIVATE);
          addField
          (linkIdClassBldr, linkIdJoinBldr, idTypeName, m2mInf.joinColumn);
          TypeName joinIdTypeName = TypeName.get(joinIdClass);
          FieldSpec.Builder linkIdInverseJoinBldr = FieldSpec.builder
          (joinIdTypeName, m2mInf.inverseJoinColumn, Modifier.PRIVATE);
          addField(
            linkIdClassBldr
          , linkIdInverseJoinBldr
          , joinIdTypeName
          , m2mInf.inverseJoinColumn
          );
          addSerializable(linkIdClassBldr, processingEnv, clazz, joinClass);
          CodeGenUtil.writeSrc
          (linkIdClassBldr, m2mInf.packageName, processingEnv);

          boolean haveJoinTable = dclField.isAnnotationPresent(JoinTable.class);

          AnnotationSpec.Builder joinManyToOne
          = AnnotationSpec.builder(ManyToOne.class)
          .addMember("fetch", "$T.$L"
          , FetchType.class, FetchType.LAZY.name());
          AnnotationSpec.Builder joinJoinColumn
          = AnnotationSpec.builder(JoinColumn.class)
          .addMember("name", "$S"
          , haveJoinTable ? m2mInf.joinColumn : m2mInf.inverseJoinColumn)
          .addMember("insertable", "$L", false)
          .addMember("updatable", "$L", false);
          FieldSpec.Builder joinField = FieldSpec.builder
          (m2mInf.joinClass, m2mInf.joinFieldName, Modifier.PRIVATE)
          .addAnnotation(joinManyToOne.build())
          .addAnnotation(joinJoinColumn.build());

          AnnotationSpec.Builder inverseJoinJoinColumn
          = AnnotationSpec.builder(JoinColumn.class)
          .addMember("name", "$S"
          , haveJoinTable ? m2mInf.inverseJoinColumn : m2mInf.joinColumn)
          .addMember("insertable", "$L", false)
          .addMember("updatable", "$L", false);
          FieldSpec.Builder inverseJoinFieldBldr = FieldSpec.builder(
            m2mInf.inverseJoinClass
          , m2mInf.inverseJoinFieldName
          , Modifier.PRIVATE
          )
          .addAnnotation(joinManyToOne.build())
          .addAnnotation(inverseJoinJoinColumn.build());

          TypeSpec.Builder linkClassBldr
          = TypeSpec.classBuilder(m2mInf.className)
          .addAnnotation(CodeGenUtil.generated
          (StringUtil.field(m2mInf.packageName, m2mInf.className)))
          .addAnnotation(ManyToManyGeneratedLink.class)
          .addAnnotation(Entity.class)
          .addAnnotation(
            AnnotationSpec.builder(Table.class)
            .addMember("name", "$S", m2mInf.table)
            .build()
          )
          .addAnnotation(
            AnnotationSpec.builder(IdClass.class)
            .addMember("value", "$N.class", linkIdClassName)
            .build()
          )
          .addAnnotation(ToString.class)
          .addAnnotation(EqualsAndHashCode.class)
          .addField(joinField.build())
          .addField(inverseJoinFieldBldr.build())
          .addMethod(allArgsCtor.build())
          .addMethod(ctor.build());
          FieldSpec.Builder joinBldr = FieldSpec.builder
          (idClass, m2mInf.joinColumn, Modifier.PRIVATE)
          .addAnnotation(Id.class);
          CodeGenUtil.addField
          (linkClassBldr, joinBldr, idTypeName, m2mInf.joinColumn, true);
          FieldSpec.Builder inverseJoinBldr = FieldSpec.builder
          (joinIdTypeName, m2mInf.inverseJoinColumn, Modifier.PRIVATE)
          .addAnnotation(Id.class);
          CodeGenUtil.addField(
            linkClassBldr
          , inverseJoinBldr
          , joinIdTypeName
          , m2mInf.inverseJoinColumn
          , true
          );
          addSerializable(linkClassBldr, processingEnv, clazz, joinClass);
          CodeGenUtil.writeSrc
          (linkClassBldr, m2mInf.packageName, processingEnv);
        } else {
          fieldName = dclName;
          fieldType = ClassName.get(dclClass);
          fieldBldr = CodeGenUtil.fieldBuilder(
            dclField
          , fieldName
          , fieldType
          , WOR_FIELD_EXCLUDED_PACKAGES
          , WOR_FIELD_EXCLUDED_ANNOTATIONS
          );
          fieldBldr.addAnnotation
          (dclField.isAnnotationPresent(Id.class) ? Id.class : Column.class);
          ctor1.addStatement("$N = $N.$N()"
          , fieldName, CodeGenUtil.PARAM_NAME, CodeGenUtil.getterName(fieldName));
          ctor2.addStatement("$N = $N.$N()"
          , fieldName, CodeGenUtil.PARAM_NAME, CodeGenUtil.getterName(fieldName));
        }
        checkAndAddActionAccess(fieldBldr, clazz, dclField);
        CodeGenUtil.addField(
          classBldr
        , fieldBldr
        , fieldType
        , fieldName
        , true
        , isIdentifiable && Identifiable.ID.equals(dclField.getName())
        , setterCode
        , joinName
        , false
        , null
        );
      }
    }
    if(idSetter != null) {
      classBldr.addMethod(idSetter.addModifiers(Modifier.PUBLIC).build());
    }
    classBldr.addMethod(ctor1.endControlFlow().build());
    classBldr.addMethod(ctor2.endControlFlow().build());
    for(Method method : Reflection.annotatedMethods
    (clazz, new Class[] { AssertTrue.class, AssertFalse.class })) {
      Annotation assertAnno = method.getAnnotation(AssertTrue.class);
      if(assertAnno == null) {
        assertAnno = method.getAnnotation(AssertFalse.class);
      }
      if(DEBUG) {
        System.out.println("assertAnno=" + assertAnno + ", method=" + method.getName());
      }
      MethodSpec.Builder assertBldr = MethodSpec.methodBuilder(method.getName())
      .addModifiers(Modifier.PUBLIC).returns(boolean.class)
      .addAnnotation(AnnotationSpec.get(assertAnno)).addAnnotation(JsonIgnore.class)
      .addStatement("return $N.$N(this)", worSimpleName + "Validation", method.getName())
      ;
      classBldr.addMethod(assertBldr.build());
    }
    CodeGenUtil.writeSrc(classBldr, clazzPackageName, processingEnv);

    ClassName clazzArgName = ClassName.get(idClass);
    ClassName filterClazzName = ClassName.get(
      Reflection.apiPackageName(clazzPackageName)
    , Reflection.filterClassName(clazzSimpleName)
    );
    TypeSpec.Builder request = CodeGenUtil.newCrudRequests
    (clazzSimpleName, clazzArgName, worClazzName, filterClazzName);
    CodeGenUtil.writeSrc(request, clazzPackageName, processingEnv);
  }

  public static void addField(
    TypeSpec.Builder classBldr
  , FieldSpec.Builder fieldBldr
  , TypeName type
  , String name
  ) {
    classBldr.addField(fieldBldr.build());
    CodeGenUtil.addGetter(classBldr, type, name, false, null);
    CodeGenUtil.addSetter
    (classBldr, type, name, false, SetterCode.SIMPLE, null, false, null);
  }

  private static void addSerializable(
    TypeSpec.Builder bldr
  , ProcessingEnvironment processingEnv
  , Class<?>... srcClasses
  ) {
    Long serialVersionUID = 0L;
    for(Class<?> c : srcClasses) {
      Long v = 0L;
      try {
        Field f = c.getDeclaredField(SERIAL_VERSION_UID);
        f.setAccessible(true);
        v = f.getLong(null);
      }
      catch(IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException ex) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING
        , c.getName() + "." + SERIAL_VERSION_UID
        + " field is not accessible, so take it equal to 0L.");
      }
      if(!serialVersionUID.equals(v)) {
        serialVersionUID = v;
      }
    }
    bldr.addSuperinterface(Serializable.class)
    .addField(FieldSpec.builder(long.class, SERIAL_VERSION_UID
    , Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
    .initializer("$L", serialVersionUID).build());
  }

  private static void checkAndAddActionAccess
  (FieldSpec.Builder fieldBldr, Class<?> parent, Field dclField) {
    RoleCalc calc = new FieldRoleCalc(parent, dclField);
    AnnotationSpec.Builder aBldr = AnnotationSpec.builder(ActionAccess.class)
    .addMember(CodeGenUtil.ANNOTATION_VALUE, "$S", calc.name())
    .addMember(ActionAccess.DESCRIPTION, "$S", calc.description())
    .addMember(ActionAccess.ID, "$LL", calc.id())
    .addMember(
      ActionAccess.DEFAULT_ACCESS
    , "$T.$L"
    , DefaultAccess.class
    , calc.defaultAccess().name()
    );
    fieldBldr.addAnnotation(aBldr.build());
    UpdateRoleCalc uCalc = new UpdateFieldRoleCalc(parent, dclField);
    AnnotationSpec.Builder uaBldr = AnnotationSpec.builder(UpdateAccess.class)
    .addMember(CodeGenUtil.ANNOTATION_VALUE, "$S", uCalc.name())
    .addMember(UpdateAccess.DESCRIPTION, "$S", uCalc.description())
    .addMember(UpdateAccess.ID, "$LL", uCalc.id())
    .addMember(
      UpdateAccess.DEFAULT_ACCESS
    , "$T.$L"
    , DefaultAccess.class
    , uCalc.defaultAccess().name()
    );
    fieldBldr.addAnnotation(uaBldr.build());
  }

}
