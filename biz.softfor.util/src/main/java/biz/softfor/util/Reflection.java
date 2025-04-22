package biz.softfor.util;

import biz.softfor.util.api.Identifiable;
import biz.softfor.util.api.ServerError;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class Reflection {

  public final static String API_PART_PACKAGE_NAME = ".api";
  public final static String JPA_PART_PACKAGE_NAME = ".jpa";

  public final static String DTO = "Dto";//data transfer object
  public final static String FLTR = "Fltr";//filter
  public final static int FLTR_LEN = FLTR.length();
  public final static String WOR = "Wor";//without relations
  public final static int WOR_LEN = WOR.length();
  public final static String HIBERNATE_PROXY = "$$_hibernate_interceptor";

  public final static int PROPERTY = Modifier.FINAL | Modifier.STATIC;

  public final static String ONLY_CLASS_METHOD_FIELD_ARE_ALLOWED
  = "Only argument of type 'Class', 'Method' or 'Field' are allowed.";

  public final static String EQUALS_METHOD = "equals";
  public final static String HASHCODE_METHOD = "hashCode";
  public final static String TOSTRING_METHOD = "toString";

  public final static String REQUEST = "Request";
  public final static String CREATE = "Create";
  public final static String READ = "Read";
  public final static String UPDATE = "Update";
  public final static String DELETE = "Delete";

  @SuppressWarnings("unchecked")
  public static List<Field> annotatedFields(Class clazz, Class... annotations) {
    List<Field> result = new ArrayList<>();
    for(; clazz != null; clazz = clazz.getSuperclass()) {
      for(Field field : clazz.getDeclaredFields()) {
        for(Class a : annotations) {
          if(field.isAnnotationPresent(a)) {
            result.add(field);
            break;
          }
        }
      }
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  public static List<Method> annotatedMethods(Class clazz, Class... annotations) {
    List<Method> result = new ArrayList<>();
    for(; clazz != null; clazz = clazz.getSuperclass()) {
      for(Method method : clazz.getDeclaredMethods()) {
        for(Class a : annotations) {
          if(method.isAnnotationPresent(a)) {
            result.add(method);
            break;
          }
        }
      }
    }
    return result;
  }

  public static String apiPackageName(String packageName) {
    String result;
    int l = packageName.lastIndexOf(JPA_PART_PACKAGE_NAME);
    if(l > 0) {
      result = packageName.substring(0, l) + API_PART_PACKAGE_NAME;
    } else {
      result = packageName.replace
      (JPA_PART_PACKAGE_NAME + ".", API_PART_PACKAGE_NAME + ".");
    }
    return result;
  }

  public static Field declaredField(Class clazz, String name) {
    Field result = null;
    try {
      result = clazz.getDeclaredField(name);
    }
    catch(NoSuchFieldException | SecurityException ex) {
    }
    for(; result == null && clazz != null; clazz = clazz.getSuperclass()) {
      for(Field field : clazz.getDeclaredFields()) {
        if(name.equalsIgnoreCase(field.getName())) {
          result = field;
          break;
        }
      }
    }
    return result;
  }

  public static List<Field> declaredProperties(Class clazz) {
    List<Field> result = new ArrayList<>();
    for(; clazz != null; clazz = clazz.getSuperclass()) {
      for(Field field : clazz.getDeclaredFields()) {
        if((field.getModifiers() & PROPERTY) == 0) {
          result.add(field);
        }
      }
    }
    return result;
  }

  public static String dtoClassName(String className) {
    return className + DTO;
  }

  public static String filterClassName(String className) {
    return className + FLTR;
  }

  public static String filterClassName(Class<?> entityClass) {
    return StringUtil.field(
      apiPackageName(entityClass.getPackageName())
    , filterClassName(entityClass.getSimpleName())
    );
  }

  public static Class<?> filterClass(Class<?> entityClass)
  throws ClassNotFoundException {
    return Class.forName(filterClassName(entityClass));
  }

  public static Class<?> worClass(Class<?> entityClass)
  throws ClassNotFoundException {
    return Class.forName(worClassName(entityClass.getName()));
  }

  public static String worClassName(String className) {
    return className + WOR;
  }

  //more details: https://habr.com/ru/post/66593/
  public static Class genericParameter(Type genericType, int num) {
    ParameterizedType parameterizedType
    = genericType instanceof ParameterizedType genericTypeAsParameterizedType
    ? genericTypeAsParameterizedType : null;
    return parameterizedType == null
    ? null : (Class)parameterizedType.getActualTypeArguments()[num];
  }

  public static Class genericParameter(Field field) {
    return genericParameter(field.getGenericType(), 0);
  }

  private final static int DOT_CLASS_LEN = ".class".length();

  public static List<Class> getClasses(String packageName) {
    List<Class> result = new ArrayList<>();
    String packagePath = packageName.replace('.', '/');
    URL packageResource = ClassLoader.getSystemClassLoader().getResource(packagePath);
    if(packageResource == null) {
      System.out.println("resource empty: " + packagePath);
    }
    if(packageResource != null) {
      try {
        Path root;
        URI packageUri = packageResource.toURI();
        if(packageUri.toString().startsWith("jar:")) {
          try {
            root = FileSystems.getFileSystem(packageUri).getPath(packagePath);
          }
          catch(FileSystemNotFoundException ex) {
            root = FileSystems.newFileSystem(packageUri, Collections.emptyMap()).getPath(packagePath);
          }
        } else {
          root = Paths.get(packageUri);
        }
        try( Stream<Path> allPaths = Files.walk(root)) {
          allPaths.filter(Files::isRegularFile).forEach(file -> {
            try {
              String path = file.toString().replace('/', '.');
              String className = path.substring
              (path.indexOf(packageName), path.length() - DOT_CLASS_LEN);
              result.add(Class.forName(className));
            }
            catch(ClassNotFoundException | StringIndexOutOfBoundsException ex) {
            }
          });
        }
      }
      catch(URISyntaxException | IOException ex) {
      }
    }
    return result;
  }

  public static PropertyDescriptor getPropertyDescriptor(
    Class clazz, String propertyName
  ) throws IntrospectionException {
    PropertyDescriptor result = null;
    for(PropertyDescriptor pd
    : Introspector.getBeanInfo(clazz).getPropertyDescriptors()) {
      if(pd.getName().equals(propertyName)) {
        result = pd;
        break;
      }
    }
    return result;
  }

  public static Set<String> getProperties(Class clazz) {
    try {
      return getPropertyNames(clazz);
    }
    catch(IntrospectionException ex) {
      throw new ServerError(ex);
    }
  }

  public static Set<String> getPropertyNames(Class clazz)
  throws IntrospectionException {
    PropertyDescriptor[] pds
    = Introspector.getBeanInfo(clazz).getPropertyDescriptors();
    Set<String> result = new HashSet<>(pds.length);
    for(PropertyDescriptor pd : pds) {
      String pn = pd.getName();
      if(!pn.equals("class")) {
        result.add(pn);
      }
    }
    return result;
  }

  public static Class<?> idClass(Class clazz) {
    Class<?> result = genericParameter(clazz.getGenericSuperclass(), 0);
    if(result == null) {
      result = declaredField(clazz, Identifiable.ID).getType();
    }
    return result;
  }

  public static boolean isProperty(int modifiers) {
    return (modifiers & PROPERTY) == 0;
  }

  public static boolean isProperty(Field field) {
    return isProperty(field.getModifiers());
  }

  public static boolean isFltrClass(Class<?> clazz) {
    return clazz.getSimpleName().endsWith(FLTR);
  }

  public static boolean isWorClass(Class<?> clazz) {
    return clazz.getSimpleName().endsWith(WOR);
  }

  public static String methodName() {
    return methodName(0);
  }

  public static String methodName(int back) {
    return StackWalker.getInstance().walk(
      frames -> frames.skip(back + 1).findFirst().map
      (f -> f.getClassName() + '#' + f.getMethodName() + ':' + f.getLineNumber())
    ).get();
  }

  public static <R> R newInstance(Class<R> resultClass) {
    try {
      return resultClass.getConstructor().newInstance();
    }
    catch(IllegalAccessException | IllegalArgumentException
    | InstantiationException | InvocationTargetException | NoSuchMethodException
    | SecurityException ex) {
      throw new ServerError(ex);
    }
  }

  public static Class superGenericParameter(Class clazz, int num) {
    return genericParameter(clazz.getGenericSuperclass(), num);
  }

  public static String trace(int l) {
    StackTraceElement[] st = Thread.currentThread().getStackTrace();
    String result = "";
    if(l < st.length) {
      StackTraceElement e = st[l];
      result = e.getClassName() + ":" + e.getLineNumber();
    }
    return result;
  }

  public static String trace() {
    return trace(4);
  }

}
