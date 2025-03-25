package biz.softfor.jpa.crud.querygraph;

import biz.softfor.util.StringUtil;
import biz.softfor.util.api.InternalResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import java.util.List;

public class UpdateCtx {

  public final EntityManager em;
  public final CriteriaBuilder cb;
  public final PredicateProvider pvdr;
  public final Class<?> rootClass;
  public final List<String> updateToNull;
  public final String parentField;
  public final InternalResponse response;
  public int count;
  public boolean isResultСounted;
  public boolean isUpdateToNull;

  public UpdateCtx(
    EntityManager em
  , CriteriaBuilder cb
  , PredicateProvider pvdr
  , Class<?> rootClass
  , List<String> updateToNull
  , String parentField
  , InternalResponse response
  ) {
    this.em = em;
    this.cb = cb;
    this.rootClass = rootClass;
    this.pvdr = pvdr;
    this.updateToNull = updateToNull;
    this.parentField = parentField;
    this.response = response;
    count = 0;
    isResultСounted = false;
  }

  public boolean isUpdateToNullField(String name) {
    return updateToNull.contains(name);
  }

  public boolean isUpdateToNullSubfield(String name) {
    boolean result = false;
    String nameWithDelimiter = name + StringUtil.FIELDS_DELIMITER;
    for(String f : updateToNull) {
      if(f.startsWith(nameWithDelimiter)) {
        result = true;
        break;
      }
    }
    return result;
  }

}
