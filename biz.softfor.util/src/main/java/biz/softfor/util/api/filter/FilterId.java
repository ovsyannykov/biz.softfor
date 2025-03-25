package biz.softfor.util.api.filter;

import java.io.Serializable;
import java.util.Set;
import lombok.ToString;

@ToString
public class FilterId<K extends Number> implements Serializable {

  private Set<K> id;

  private Object and;
  public final static String AND = "and";

  private final static long serialVersionUID = 0L;

  public Set<K> getId() {
    return id;
  }

  public void setId(Set<K> id) {
    this.id = id;
  }

  public void assignId(K... id) {
    this.id = Set.of(id);
  }

  public Object and() {
    return and;
  }

  public void and(Object... and) {
    this.and = and;
  }

  public void andAnd(Object... and) {
    if(this.and == null) {
      this.and = and;
    } else {
      this.and = new Object[] { this.and, and };
    }
  }

  public void andReset() {
    this.and = null;
  }

  public void reset() {
    id = null;
    andReset();
  }

}
