package biz.softfor.jpa.crud.querygraph;

import biz.softfor.util.Generated;
import biz.softfor.util.Reflection;
import biz.softfor.util.api.CreateRequest;
import biz.softfor.util.api.DeleteRequest;
import biz.softfor.util.api.ReadRequest;
import biz.softfor.util.api.UpdateRequest;
import java.util.Map;

public class EntityInf {

  public final Class<?> clazz;
  public final Class<?> worClass;
  public final Class<?> fltrClass;
  public final Map<String, ColumnDescr> cds;
  public final Class<? extends CreateRequest> createRequestClass;
  public final Class<? extends ReadRequest> readRequestClass;
  public final Class<? extends UpdateRequest> updateRequestClass;
  public final Class<? extends DeleteRequest> deleteRequestClass;

  public EntityInf(Class<?> clazz, Map<String, ColumnDescr> cds)
  throws ReflectiveOperationException {
    boolean isM2MLink = false;
    if(Reflection.isFltrClass(clazz)) {
      String clazzName = clazz.getAnnotation(Generated.class).value();
      this.clazz = Class.forName(clazzName);
      worClass = Reflection.worClass(this.clazz);
      fltrClass = clazz;
    } else if(Reflection.isWorClass(clazz)) {
      String clazzName = clazz.getAnnotation(Generated.class).value();
      this.clazz = Class.forName(clazzName);
      worClass = clazz;
      fltrClass = Reflection.filterClass(this.clazz);
    } else {
      this.clazz = clazz;
      isM2MLink = this.clazz.isAnnotationPresent(ManyToManyGeneratedLink.class)
      || this.clazz.isAnnotationPresent(TestEntity.class);
      if(isM2MLink) {
        worClass = null;
        fltrClass = null;
      } else {
        worClass = Reflection.worClass(this.clazz);
        fltrClass = Reflection.filterClass(this.clazz);
      }
    }
    this.cds = cds;
    if(isM2MLink) {
      createRequestClass = null;
      readRequestClass = null;
      updateRequestClass = null;
      deleteRequestClass = null;
    } else {
      String clazzName = this.clazz.getName();
      createRequestClass = (Class<? extends CreateRequest>)
      Class.forName(clazzName + Reflection.REQUEST + "$" + Reflection.CREATE);
      readRequestClass = (Class<? extends ReadRequest>)
      Class.forName(clazzName + Reflection.REQUEST + "$" + Reflection.READ);
      updateRequestClass = (Class<? extends UpdateRequest>)
      Class.forName(clazzName + Reflection.REQUEST + "$" + Reflection.UPDATE);
      deleteRequestClass = (Class<? extends DeleteRequest>)
      Class.forName(clazzName + Reflection.REQUEST + "$" + Reflection.DELETE);
    }
  }

}
