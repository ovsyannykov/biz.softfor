package biz.softfor.spring.servicegen;

import biz.softfor.codegen.CodeGen;
import biz.softfor.codegen.CodeGenUtil;
import biz.softfor.spring.jpa.crud.CrudSvc;
import biz.softfor.util.Reflection;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import org.springframework.stereotype.Service;

public class ServiceGen extends CodeGen {

  private final static String SERVICE_PART_PACKAGE_NAME = ".spring";
  private final static String SERVICE_SFX = "Svc";
  private List<String> exclude;

  public ServiceGen() {
    super(GenService.class);
  }

  @Override
  protected void preProcess(Element element) {
    exclude = new ArrayList<>();
    AnnotationValue av = CodeGenUtil.getAnnotationProperty
    (element, supportedAnnotation, CodeGenUtil.ANNOTATION_EXCLUDE);
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
        (Reflection.JPA_PART_PACKAGE_NAME, SERVICE_PART_PACKAGE_NAME)
      , clazzSimpleName + SERVICE_SFX
      );
      if(!exclude.contains(className.canonicalName())) {
        Class<?> idClass = Reflection.idClass(clazz);
        ClassName worClazzName = ClassName.get
        (clazzPackageName, Reflection.worClassName(clazzSimpleName));
        ClassName filterClazzName = ClassName.get(
          Reflection.apiPackageName(clazzPackageName)
        , Reflection.filterClassName(clazzSimpleName)
        );
        TypeSpec.Builder classBldr = TypeSpec.classBuilder(className)
        .superclass(ParameterizedTypeName.get(
          ClassName.get(CrudSvc.class)
        , ClassName.get(idClass)
        , ClassName.get(clazz)
        , worClazzName
        , filterClazzName
        ))
        .addAnnotation(Service.class)
        ;
        CodeGenUtil.writeSrc(classBldr, className.packageName(), processingEnv);
      }
    }
  }

}
