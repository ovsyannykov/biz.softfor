package biz.softfor.jpa;

import biz.softfor.util.api.Identifiable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Objects;
import lombok.ToString;

@MappedSuperclass
@ToString
@JsonIgnoreProperties({ "handler", "hibernateLazyInitializer", "this$0" })
public class IdEntity<K extends Number> implements Identifiable<K>, Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private K id;

  private final static long serialVersionUID = 0L;

  public IdEntity(K id) {
    this.id = id;
  }

  public IdEntity() {
    this.id = null;
  }

  public IdEntity(IdEntity<K> v) {
    this(v == null ? null : v.getId());
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
