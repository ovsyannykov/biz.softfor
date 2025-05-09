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
import org.hibernate.proxy.HibernateProxy;

//https://jpa-buddy.com/blog/hopefully-the-final-article-about-equals-and-hashcode-for-jpa-entities-with-db-generated-ids/
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
  public final boolean equals(Object a) {
    boolean result;
    if(a == this) {
      result = true;
    } else if(a == null) {
      result = false;
    } else {
      Class<?> effectiveClass = a instanceof HibernateProxy
      ? ((HibernateProxy)a).getHibernateLazyInitializer().getPersistentClass()
      : a.getClass();
      Class<?> clazz = this instanceof HibernateProxy
      ? ((HibernateProxy)this).getHibernateLazyInitializer().getPersistentClass()
      : this.getClass();
      if(clazz == effectiveClass) {
        Object tid = getId();
        result = tid != null && Objects.equals(tid, ((Identifiable)a).getId());
      } else {
        result = false;
      }
    }
    return result;
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy
    ? ((HibernateProxy)this).getHibernateLazyInitializer().getPersistentClass()
    .hashCode() : getClass().hashCode();
  }

}
