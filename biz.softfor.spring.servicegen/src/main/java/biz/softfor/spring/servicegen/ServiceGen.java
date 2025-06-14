package biz.softfor.spring.servicegen;

import biz.softfor.codegen.CodeGen;
import biz.softfor.codegen.CodeGenUtil;
import biz.softfor.spring.jpa.crud.CrudSvc;
import biz.softfor.util.Reflection;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import org.springframework.stereotype.Service;

public class ServiceGen extends CodeGen {

  private List<String> exclude;

  public ServiceGen() {
    super(GenService.class);
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
      ClassName className = (ClassName)CodeGenUtil.svcClassName(clazz);
      if(!exclude.contains(className.canonicalName())) {
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
  }

}
