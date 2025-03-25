package biz.softfor.jpa;

import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
@SuppressWarnings("EqualsAndHashcode")
public class SetStoredEntity<K extends Number> extends IdEntity<K>
implements Serializable {

  private final static long serialVersionUID = 0L;

  public SetStoredEntity(K id) {
    super(id);
  }

  public SetStoredEntity() {
  }

  public SetStoredEntity(SetStoredEntity<K> v) {
    this(v == null ? null : v.getId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

}
