package biz.softfor.codegen;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

public abstract class CodeGen extends AbstractProcessor {

  protected final Set<String> supportedAnnotationTypes;
  private final Class<?> classWithProcessingEntities;

  protected CodeGen
  (String supportedAnnotation, Class<?> classWithProcessingEntities) {
    supportedAnnotationTypes = Set.of(supportedAnnotation);
    this.classWithProcessingEntities = classWithProcessingEntities;
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
    try {
      Field processed
      = classWithProcessingEntities.getField(CodeGenUtil.PROCESSED_FIELD_NAME);
      if(!processed.getBoolean(null)) {
        for(Class<?> clazz : (Class<?>[])classWithProcessingEntities
        .getField(CodeGenUtil.ENTITIES_FIELD_NAME).get(null)) {
          process(clazz);
        }
        processed.setBoolean(null, true);
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

  protected abstract void process(Class<?> clazz) throws IllegalAccessException
  , IllegalArgumentException, InvocationTargetException, NoSuchFieldException
  , NoSuchMethodException, SecurityException;

}
