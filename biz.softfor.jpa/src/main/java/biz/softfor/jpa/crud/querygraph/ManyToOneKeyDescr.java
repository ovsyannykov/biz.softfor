package biz.softfor.jpa.crud.querygraph;

import biz.softfor.util.api.Identifiable;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Path;
import java.lang.reflect.Field;

class ManyToOneKeyDescr extends ManyToOneWorDescr {

  protected ManyToOneKeyDescr(Class<?> parent, Field field) {
    super(parent, field);
  }

  @Override
  protected Path<?> getPath(From root) {
    return root.get(fieldName).get(Identifiable.ID);
  }

}
