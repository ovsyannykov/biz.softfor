package biz.softfor.spring.jpa.crud.assets;

import biz.softfor.spring.sqllog.SqlCountValidator;
import biz.softfor.testutil.jpa.TestEntities;
import biz.softfor.util.api.Identifiable;
import biz.softfor.util.api.filter.Expr;
import biz.softfor.util.api.filter.FilterId;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.function.TriConsumer;
import org.apache.commons.lang3.function.TriFunction;

public class UpdateParams {

  public final static BiFunction<Object, String, Object> NOP = (e, label) -> e;
  public final static String UPDATED = "Updated";
  public final static TriFunction<String, Object, Object, Object>
  UPDATE_PROPERTY = (propertyName, e, value) -> {
    try {
      PropertyUtils.setProperty(e, propertyName, value);
    }
    catch(IllegalAccessException | InvocationTargetException
    | NoSuchMethodException ex) {
      throw new RuntimeException(ex);
    }
    return e;
  };
  public final static TriFunction<String, Object, String, Object> UPDATE
  = (propertyName, e, label)
  -> UPDATE_PROPERTY.apply(propertyName, e, label + UpdateParams.UPDATED);
  public final static TriConsumer<FilterId, Collection<Integer>, TestEntities>
  BY_ID = (filter, idxs, data)
  -> filter.setId(data.idList(idxs));
  public final static TriConsumer<FilterId, Collection<Integer>, TestEntities>
  BY_AND = (filter, idxs, data)
  -> filter.and(new Expr(Expr.IN, Identifiable.ID, data.idList(idxs)));

  public final String description;
  public final Collection<Integer> idxs;
  public final Collection<Integer> joinIdxs;
  public final BiFunction<Object, String, Object> updater;
  public final List<String> fields;
  public final TriConsumer<FilterId, Collection<Integer>, TestEntities> filter;
  public final int total;
  public final SqlCountValidator.Builder validatorBldr;

  public UpdateParams(
    String description
  , Collection<Integer> idxs
  , Collection<Integer> joinIdxs
  , BiFunction<Object, String, Object> updater
  , List<String> fields
  , TriConsumer<FilterId, Collection<Integer>, TestEntities> filter
  , int total
  , SqlCountValidator.Builder validatorBldr
  ) {
    this.description = description;
    this.idxs = idxs;
    this.joinIdxs = joinIdxs;
    this.updater = updater;
    this.fields = fields;
    this.filter = filter;
    this.total = total;
    this.validatorBldr = validatorBldr;
  }

  public UpdateParams(
    String description
  , Collection<Integer> sourceIdxs
  , Collection<Integer> joinIdxs
  , BiFunction<Object, String, Object> updater
  , TriConsumer<FilterId, Collection<Integer>, TestEntities> filter
  , int total
  , SqlCountValidator.Builder validatorBldr
  ) {
    this
    (description, sourceIdxs, joinIdxs, updater, null, filter, total, validatorBldr);
  }

  public UpdateParams(
    String description
  , Collection<Integer> sourceIdxs
  , Collection<Integer> joinedIdxs
  , BiFunction<Object, String, Object> updater
  , int total
  , SqlCountValidator.Builder validatorBldr
  ) {
    this
    (description, sourceIdxs, joinedIdxs, updater, null, BY_ID, total, validatorBldr);
  }

}
