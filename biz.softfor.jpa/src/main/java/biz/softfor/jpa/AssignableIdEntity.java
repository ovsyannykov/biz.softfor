package biz.softfor.jpa;

import biz.softfor.util.api.Identifiable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Objects;
import lombok.ToString;

@MappedSuperclass
@ToString
@JsonIgnoreProperties({ "handler", "hibernateLazyInitializer" })
public class AssignableIdEntity<K extends Number>
implements Identifiable<K>, Serializable {

  @Id
  private K id;

  private final static long serialVersionUID = 0L;

  public AssignableIdEntity(K id) {
    this.id = id;
  }

  public AssignableIdEntity() {
    id = null;
  }

  public AssignableIdEntity(AssignableIdEntity<K> v) {
    id = v == null ? null : v.getId();
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
    return Objects.hashCode(getId());
  }

}
