package biz.softfor.spring.restcontrollergen;

import biz.softfor.codegen.CodeGen;
import biz.softfor.codegen.CodeGenUtil;
import biz.softfor.jpa.crud.AbstractCrudSvc;
import biz.softfor.spring.security.SecurityUtil;
import biz.softfor.spring.security.service.JsonFilters;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.util.Create;
import biz.softfor.util.Reflection;
import biz.softfor.util.api.CommonResponse;
import biz.softfor.util.api.StdPath;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import jakarta.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

public class RestControllerGen extends CodeGen {

  private final static String groups = "groups()";
  private final static String jsonFilters = "jsonFilters";
  private final static String methodCheck = "methodCheck";
  private final static String request = "request";
  private final static String RESTCONTROLLER_PART_PACKAGE_NAME = ".spring.rest";
  private final static String securityMgr = "securityMgr";
  private final static String service = "service";

  private List<String> exclude;

  public RestControllerGen() {
    super(GenRestController.class);
  }

  @Override
  protected void preProcess(Element element) {
    exclude = new ArrayList<>();
    AnnotationValue av = CodeGenUtil.getAnnotationProperty
    (element, supportedAnnotation, CodeGenUtil.EXCLUDE_ANNO_PROP);
    if(av != null) {
      List<? extends AnnotationValue> avs
      = (List<? extends AnnotationValue>)av.getValue();
      for(AnnotationValue v : avs) {
        exclude.add(v.getValue().toString());
      }
    }
  }

  @Override
  public void process(Class<?> clazz) throws IllegalAccessException
  , IllegalArgumentException, InvocationTargetException, NoSuchFieldException
  , NoSuchMethodException, SecurityException {
    if(!CodeGenUtil.isLinkClass(clazz)) {
      String clazzPackageName = clazz.getPackageName();
      String clazzSimpleName = clazz.getSimpleName();
      ClassName className = ClassName.get(
        clazzPackageName.replace
        (Reflection.JPA_PART_PACKAGE_NAME, RESTCONTROLLER_PART_PACKAGE_NAME)
      , clazzSimpleName + CodeGenUtil.RESTCONTROLLER_SFX
      );
      if(!exclude.contains(className.canonicalName())) {
        ClassName worClazzName = CodeGenUtil.worClassName(clazz);
        ClassName jsonFiltersClass = ClassName.get(JsonFilters.class);
        ClassName securityMgrClass = ClassName.get(SecurityMgr.class);
        ClassName serviceClass = CodeGenUtil.svcClassName(clazz);
        TypeSpec.Builder classBldr = TypeSpec.classBuilder(className)
        .addModifiers(Modifier.PUBLIC)
        .addAnnotation(RestController.class)
        .addAnnotation(
          AnnotationSpec.builder(RequestMapping.class)
          .addMember(
            CodeGenUtil.PATH_ANNO_PROP
          , "$S"
          , StdPath.ROOT + StringUtils.uncapitalize(clazzSimpleName)
          )
          .addMember("produces", "$S", MediaType.APPLICATION_JSON_VALUE)
          .build()
        )
        .addField(jsonFiltersClass, jsonFilters, Modifier.PRIVATE, Modifier.FINAL)
        .addField(securityMgrClass, securityMgr, Modifier.PRIVATE, Modifier.FINAL)
        .addField(serviceClass, service, Modifier.PRIVATE, Modifier.FINAL)
        .addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)
          .addParameter(jsonFiltersClass, jsonFilters)
          .addParameter(securityMgrClass, securityMgr)
          .addParameter(serviceClass, service)
          .addStatement("this.$N=$N", jsonFilters, jsonFilters)
          .addStatement("this.$N=$N", securityMgr, securityMgr)
          .addStatement("this.$N=$N", service, service)
          .build()
        )
        .addMethod(MethodSpec.methodBuilder(AbstractCrudSvc.CREATE_METHOD)
          .addModifiers(Modifier.PUBLIC)
          .returns(ParameterizedTypeName.get(
            ClassName.get(CommonResponse.class), worClazzName
          )) 
          .addAnnotation(requestMapping(StdPath.CREATE))
          .addParameter(
            request(clazz, Reflection.CREATE)
            .addAnnotation(
              AnnotationSpec.builder(Validated.class)
              .addMember(CodeGenUtil.VALUE_ANNO_PROP, "$T.class", Create.class)
              .build()
            )
            .build()
          )
          .addStatement(securityMgr + "." + methodCheck + "(" + service
            + ".serviceClass(), $S, $T." + groups + ")"
          , AbstractCrudSvc.CREATE_METHOD
          , SecurityUtil.class
          )
          .addStatement("return " + service + "."
          + AbstractCrudSvc.CREATE_METHOD + "(" + request + ")")
          .build()
        )
        .addMethod(MethodSpec.methodBuilder(AbstractCrudSvc.READ_METHOD)
          .addModifiers(Modifier.PUBLIC)
          .returns(MappingJacksonValue.class) 
          .addAnnotation(requestMapping(StdPath.READ))
          .addParameter(request(clazz, Reflection.READ).build())
          .addStatement(securityMgr + ".readCheck(" + service + ", " + request
            + ", $T." + groups + ")"
          , SecurityUtil.class
          )
          .addStatement("return " + jsonFilters + ".filter(" + service + "::"
          + AbstractCrudSvc.READ_METHOD + ", " + request + ", " + service
          + ".clazz())")
          .build()
        )
        .addMethod(MethodSpec.methodBuilder(AbstractCrudSvc.UPDATE_METHOD)
          .addModifiers(Modifier.PUBLIC)
          .returns(CommonResponse.class) 
          .addAnnotation(requestMapping(StdPath.UPDATE))
          .addParameter
          (request(clazz, Reflection.UPDATE).addAnnotation(Valid.class).build())
          .addStatement(securityMgr + ".updateCheck(" + service + ", " + request
            + ", $T." + groups + ")"
          , SecurityUtil.class
          )
          .addStatement(service + ".validateUpdate(" + request + ")")
          .addStatement("return " + service + "."
          + AbstractCrudSvc.UPDATE_METHOD + "(" + request + ")")
          .build()
        )
        .addMethod(MethodSpec.methodBuilder(AbstractCrudSvc.DELETE_METHOD)
          .addModifiers(Modifier.PUBLIC)
          .returns(CommonResponse.class) 
          .addAnnotation(requestMapping(StdPath.DELETE))
          .addParameter(request(clazz, Reflection.DELETE).build())
          .addStatement(securityMgr + "." + methodCheck + "(" + service
            + ".serviceClass(), $S, $T." + groups + ")"
          , AbstractCrudSvc.DELETE_METHOD
          , SecurityUtil.class
          )
          .addStatement("return " + service + "."
          + AbstractCrudSvc.DELETE_METHOD + "(" + request + ")")
          .build()
        )
        ;
        CodeGenUtil.writeSrc(classBldr, className.packageName(), processingEnv);
      }
    }
  }

  private static AnnotationSpec requestMapping(String method) {
    return AnnotationSpec.builder(RequestMapping.class)
    .addMember(CodeGenUtil.PATH_ANNO_PROP, "$S", "/" + method)
    .addMember(
      CodeGenUtil.METHOD_ANNO_PROP
    , "$T.$L"
    , RequestMethod.class
    , RequestMethod.POST
    )
    .build();
  }

  private static ParameterSpec.Builder request(Class<?> clazz, String type) {
    return ParameterSpec.builder(
      ClassName.get
      (clazz.getPackageName(), clazz.getSimpleName() + Reflection.REQUEST, type)
    , request
    )
    .addAnnotation(RequestBody.class);
  }

}
