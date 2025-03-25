package biz.softfor.jpa.annotatedclassesgen;
/*
        <configuration>
          <compilerArgs>
            <arg>--add-exports</arg>
            <arg>jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED</arg>
            <arg>--add-exports</arg>
            <arg>jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED</arg>
            <arg>--add-exports</arg>
            <arg>jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED</arg>
            <arg>--add-exports</arg>
            <arg>jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED</arg>

            <arg>-J--add-opens=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED</arg>
            <arg>-J--add-opens=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED</arg>
            <arg>-J--add-opens=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED</arg>
            <arg>-J--add-opens=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED</arg>
          </compilerArgs>
        </configuration>

import com.sun.source.util.Trees;
import com.sun.tools.javac.tree.JCTree;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementScanner6;
import javax.tools.Diagnostic;

//add to process method:
//element.accept(new EntityVisitor(processingEnv), null);
public class EntityVisitor extends ElementScanner6<Void, Void> {

  private final ProcessingEnvironment env;
  private final Messager messager;
  private final Trees trees;

  public EntityVisitor(ProcessingEnvironment env) {
    super();
    this.env = env;
    messager = env.getMessager();
    trees = Trees.instance(env);
  }

  @Override
  public Void visitType(TypeElement element, Void aVoid) {
    ((JCTree)trees.getTree(element)).accept(new EntityTranslator(env));
    if(0==1) messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, element.toString() + "=" + element.getClass().getName());
    return super.visitType(element, aVoid);
  }

}
*/