package biz.softfor.codegen;

import biz.softfor.util.Reflection;
import jakarta.persistence.Entity;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

public abstract class CodeGen extends AbstractProcessor {

  protected final Class<? extends Annotation> supportedAnnotation;
  protected final Set<String> supportedAnnotationTypes;

  protected CodeGen(Class<? extends Annotation> supportedAnnotation) {
    this.supportedAnnotation = supportedAnnotation;
    supportedAnnotationTypes = Set.of(supportedAnnotation.getName());
  }

	@Override
	public Set<String> getSupportedAnnotationTypes() {
    return supportedAnnotationTypes;
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latest();
  }

  @Override
  public boolean process
  (Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    Set<? extends Element> annotatedElements
    = roundEnv.getElementsAnnotatedWith(supportedAnnotation);
    try {
      for(Element element : annotatedElements) {
        preProcess(element);
        List<String> exclude = new ArrayList<>();
        AnnotationValue eav = CodeGenUtil.getAnnotationProperty
        (element, supportedAnnotation, CodeGenUtil.EXCLUDE_ANNO_PROP);
        if(eav != null) {
          List<? extends AnnotationValue> avs
          = (List<? extends AnnotationValue>)eav.getValue();
          for(AnnotationValue v : avs) {
            exclude.add(v.getValue().toString());
          }
        }
        AnnotationValue av = CodeGenUtil.getAnnotationProperty
        (element, supportedAnnotation, CodeGenUtil.VALUE_ANNO_PROP);
        @SuppressWarnings("unchecked")
        List<? extends AnnotationValue> avs
        = (List<? extends AnnotationValue>)av.getValue();
        String[] packages = new String[avs.size()];
        for(int i = 0; i < packages.length; ++i) {
          String className = avs.get(i).getValue().toString();
          packages[i] = className.substring(0, className.lastIndexOf('.'));
        }
        FilterBuilder fb = new FilterBuilder();
        for(String p : packages) {
          fb.includePackage(p);
        }
        ConfigurationBuilder cb = new ConfigurationBuilder().forPackages(packages)
        .filterInputsBy(fb).setScanners(Scanners.TypesAnnotated);
        Reflections reflections = new Reflections(cb);
        for(Class<?> entity : reflections.getTypesAnnotatedWith(Entity.class)) {
          if(!Reflection.isWorClass(entity) && !exclude.contains(entity.getName())
          && !CodeGenUtil.isLinkClass(entity)) {
            process(entity);
          }
        }
      }
    }
    catch(IllegalAccessException | IllegalArgumentException
    | InvocationTargetException | NoSuchFieldException | NoSuchMethodException
    | SecurityException ex) {
      processingEnv.getMessager()
      .printMessage(Diagnostic.Kind.ERROR, ex.getMessage());
    }
    return false;
  }

  protected void preProcess(Element element) {
  }

  protected abstract void process(Class<?> clazz) throws IllegalAccessException
  , IllegalArgumentException, InvocationTargetException, NoSuchFieldException
  , NoSuchMethodException, SecurityException;

}
