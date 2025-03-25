package biz.softfor.util.api;

import biz.softfor.util.Reflection;
import biz.softfor.util.api.filter.FilterId;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DeleteRequest<K extends Number, F extends FilterId<K>>
extends AbstractRequest {

  @NotNull
  public F filter;
  public final static String FILTER = "filter";

  public DeleteRequest() {
    Class<F> filterClass = Reflection.superGenericParameter(this.getClass(), 1);
    filter = Reflection.newInstance(filterClass);
  }

}
