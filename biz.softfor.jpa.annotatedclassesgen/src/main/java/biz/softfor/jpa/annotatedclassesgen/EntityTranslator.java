package biz.softfor.jpa.annotatedclassesgen;
/*
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;

public class EntityTranslator extends TreeTranslator {

  private final TreeMaker make;
  private final Names names;
  private final Types types;
  private final Messager messager;

  public EntityTranslator(ProcessingEnvironment env) {
    Context context = ((JavacProcessingEnvironment)env).getContext();
    make = TreeMaker.instance(context);
    names = Names.instance(context);
    types = Types.instance(context);
    messager = env.getMessager();
  }

  @Override
  public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
    messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, "-> visitClassDef(" + jcClassDecl.name + ")");
    JCTree.JCModifiers mods = make.Modifiers(Flags.PUBLIC | Flags.STATIC);
    Name name = names.fromString("tezzt");
    JCTree.JCExpression vartype = 0==1
    ? make.Ident(names.fromString(1==1 ? "Integer" : "java.lang.Integer"))
    : make.Select(make.Ident(names.fromString("java.util")), names.fromString("List"));

    JCTree.JCVariableDecl varDecl = make.VarDef(mods, name, vartype, null);
    jcClassDecl.defs = jcClassDecl.defs.append(varDecl);
    super.visitClassDef(jcClassDecl);
    if(0 == 1) for(JCTree def : jcClassDecl.defs) {
      messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, def.getTag().toString());
    }
    messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, "visitClassDef(" + jcClassDecl.name + ") ->");
  }

}
*/