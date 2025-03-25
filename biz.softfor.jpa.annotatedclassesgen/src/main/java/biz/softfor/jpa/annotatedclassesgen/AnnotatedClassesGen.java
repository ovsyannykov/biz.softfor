package biz.softfor.jpa.annotatedclassesgen;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

public class AnnotatedClassesGen extends AbstractProcessor {

  private final Map<String, ListGenConfig> configs = new HashMap<>();
  private final Set<String> supportedAnnotationTypes = new HashSet<>();

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    for(Map.Entry<String, String> option : processingEnv.getOptions().entrySet()) {
      String annotationName = option.getKey();
      configs.put(annotationName, new ListGenConfig(annotationName, option.getValue()));
      supportedAnnotationTypes.add(annotationName);
    }
    if(supportedAnnotationTypes.isEmpty()) {
      processingEnv.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING
      , "No annotation classes list generation option not specified like this:"
      + "\n-AannotationName=packageName.className.fieldName"
      + "\nYou can add this to the pom.xml:\n"
      + "\t<build>"
      + "\n..."
      + "\n\t\t<plugins>"
      + "\n..."
      + "\n\t\t\t<plugin>"
      + "\n\t\t\t\t<groupId>org.apache.maven.plugins</groupId>"
      + "\n\t\t\t\t<artifactId>maven-compiler-plugin</artifactId>"
      + "\n\t\t\t\t<configuration>"
      + "\n\t\t\t\t\t<compilerArgs>"
      + "\n\t\t\t\t\t\t<arg>-AannotationName=packageName.className.fieldName</arg>"
      + "\n\t\t\t\t\t</compilerArgs>"
      + "\n\t\t\t\t</configuration>"
      + "\n\t\t\t</plugin>"
      + "\n..."
      + "\n\t\t</plugins>"
      + "\n..."
      + "\n\t</build>"
      );
    }
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    for(TypeElement annotation : annotations) {
      configs.get(annotation.getQualifiedName().toString()).process(annotation, roundEnv, processingEnv);
    }
    return false;
  }

	@Override
	public Set<String> getSupportedAnnotationTypes() {
    return supportedAnnotationTypes;
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latest();
  }

}
