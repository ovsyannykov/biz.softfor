package biz.softfor.jpa.annotatedclassesgen;

import biz.softfor.codegen.CodeGenUtil;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeSpec;
import java.util.ArrayList;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import org.apache.commons.lang3.StringUtils;

public class ListGenConfig {

  public final String annotationName;
  public final String packageName;
  public final String className;
  public final String fieldName;
  public final boolean isValid;

  public ListGenConfig(String annotationName, String fullFieldName) {
    this.annotationName = annotationName;
    int classNameEnd = fullFieldName.lastIndexOf('.');
    isValid = classNameEnd > 0;
    if(isValid) {
      fieldName = fullFieldName.substring(classNameEnd + 1);
      String fullClassName = fullFieldName.substring(0, classNameEnd);
      int packageNameEnd = fullClassName.lastIndexOf('.');
      className = fullClassName.substring(packageNameEnd + 1);
      packageName = packageNameEnd >= 0 ? fullClassName.substring(0, packageNameEnd) : "";
    } else {
      fieldName = null;
      className = null;
      packageName = null;
    }
  }

  public void process(
    TypeElement annotatedElement
  , RoundEnvironment roundEnv
  , ProcessingEnvironment processingEnv
  ) {
    if(isValid) {
      ArrayList<TypeMirror> names = new ArrayList<>();
      for(Element element : roundEnv.getElementsAnnotatedWith(annotatedElement)) {
        names.add(((TypeElement)element).asType());
      }
      if(!names.isEmpty()) {
        FieldSpec.Builder entities = FieldSpec.builder(
          ArrayTypeName.of(Class.class)
        , fieldName
        , Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC
        )
        .initializer(
          "{" + StringUtils.repeat("$T.class", ",", names.size()) + "}"
        , names.toArray()
        );
        FieldSpec.Builder processed = FieldSpec.builder(boolean.class
        , CodeGenUtil.PROCESSED_FIELD_NAME
        , Modifier.PUBLIC, Modifier.STATIC
        )
        .initializer("false");
        TypeSpec.Builder bldr = TypeSpec.classBuilder(className)
        .addField(entities.build()).addField(processed.build());
        CodeGenUtil.writeSrc(bldr, packageName, processingEnv);
      }
    }
  }

}
