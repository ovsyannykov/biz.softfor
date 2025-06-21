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
import com.palantir.javapoet.AnnotationSpec;
import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.MethodSpec;
import com.palantir.javapoet.ParameterSpec;
import com.palantir.javapoet.ParameterizedTypeName;
import com.palantir.javapoet.TypeSpec;
import jakarta.validation.Valid;
import java.lang.reflect.InvocationTargetException;
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
  private final static String securityMgr = "securityMgr";
  private final static String service = "service";

  public RestControllerGen() {
    super(GenRestController.class);
  }

  @Override
  public void process(Class<?> clazz) throws IllegalAccessException
  , IllegalArgumentException, InvocationTargetException, NoSuchFieldException
  , NoSuchMethodException, SecurityException {
    ClassName className = CodeGenUtil.restControllerClassName(clazz);
    ClassName jsonFiltersClass = ClassName.get(JsonFilters.class);
    ClassName securityMgrClass = ClassName.get(SecurityMgr.class);
    ClassName serviceClass = CodeGenUtil.svcClassName(clazz);
    String COPY_PARAM = "this.$N=$N";
    TypeSpec.Builder classBldr = TypeSpec.classBuilder(className)
    .addModifiers(Modifier.PUBLIC)
    .addAnnotation(RestController.class)
    .addAnnotation(
      AnnotationSpec.builder(RequestMapping.class)
      .addMember(
        CodeGenUtil.PATH_ANNO_PROP
      , "$S"
      , "/" + StringUtils.uncapitalize(clazz.getSimpleName())
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
      .addStatement(COPY_PARAM, jsonFilters, jsonFilters)
      .addStatement(COPY_PARAM, securityMgr, securityMgr)
      .addStatement(COPY_PARAM, service, service)
      .build()
    )
    .addMethod(MethodSpec.methodBuilder(AbstractCrudSvc.CREATE_METHOD)
      .addModifiers(Modifier.PUBLIC)
      .returns(ParameterizedTypeName.get(
        ClassName.get(CommonResponse.class), CodeGenUtil.worClassName(clazz)
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
      .addStatement(
        securityMgr + ".createCheck(" + service + ", $T." + groups + ")"
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
      + AbstractCrudSvc.READ_METHOD + ", " + request + ", $T.class)", clazz)
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
      .addStatement(
        securityMgr + ".deleteCheck(" + service + ", $T." + groups + ")"
      , SecurityUtil.class
      )
      .addStatement("return " + service + "."
      + AbstractCrudSvc.DELETE_METHOD + "(" + request + ")")
      .build()
    )
    ;
    CodeGenUtil.writeSrc(classBldr, className.packageName(), processingEnv);
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
