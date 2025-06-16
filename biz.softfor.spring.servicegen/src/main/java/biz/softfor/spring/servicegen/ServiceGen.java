package biz.softfor.spring.servicegen;

import biz.softfor.codegen.CodeGen;
import biz.softfor.codegen.CodeGenUtil;
import biz.softfor.spring.jpa.crud.CrudSvc;
import biz.softfor.util.Reflection;
import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.ParameterizedTypeName;
import com.palantir.javapoet.TypeSpec;
import java.lang.reflect.InvocationTargetException;
import org.springframework.stereotype.Service;

public class ServiceGen extends CodeGen {

  public ServiceGen() {
    super(GenService.class);
  }

  @Override
  public void process(Class<?> clazz) throws IllegalAccessException
  , IllegalArgumentException, InvocationTargetException, NoSuchFieldException
  , NoSuchMethodException, SecurityException {
    ClassName className = (ClassName)CodeGenUtil.svcClassName(clazz);
    TypeSpec.Builder classBldr = TypeSpec.classBuilder(className)
    .superclass(ParameterizedTypeName.get(
      ClassName.get(CrudSvc.class)
    , ClassName.get(Reflection.idClass(clazz))
    , ClassName.get(clazz)
    , CodeGenUtil.worClassName(clazz)
    , CodeGenUtil.filterClassName(clazz)
    ))
    .addAnnotation(Service.class)
    ;
    CodeGenUtil.writeSrc(classBldr, className.packageName(), processingEnv);
  }

}
