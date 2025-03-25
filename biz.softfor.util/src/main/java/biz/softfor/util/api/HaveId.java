package biz.softfor.util.api;

import java.io.Serializable;
import java.util.Objects;
import lombok.ToString;

@ToString
public class HaveId<K extends Number> implements Identifiable<K>, Serializable {

  private K id;

  private final static long serialVersionUID = 0L;

  public HaveId(K id) {
    this.id = id;
  }

  public HaveId() {
    this(null);
  }

  @Override
  public K getId() {
    return id;
  }

  @Override
  public void setId(K id) {
    this.id = id;
  }

  @Override
  @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
  public boolean equals(Object a) {
    return Identifiable.equals(this, a);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

}
