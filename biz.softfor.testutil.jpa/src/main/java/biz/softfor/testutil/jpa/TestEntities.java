package biz.softfor.testutil.jpa;

import biz.softfor.jpa.IdEntity;
import biz.softfor.testutil.Check;
import biz.softfor.testutil.IgnoringFields;
import biz.softfor.util.Json;
import biz.softfor.util.api.Identifiable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.extern.java.Log;
import org.apache.commons.lang3.ArrayUtils;
import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Log
public class TestEntities<K extends Number, E extends Identifiable<K>> {

  public final static String[] IGNORED_FIELDS = IdEntity.class.getAnnotation(JsonIgnoreProperties.class).value();

  public final String title;
  public final String label;
  public final int size;
  public final Class<E> entityClass;
  public final BiFunction<String, Object, E> newEntity;
  public final Function<Iterable<K>, List<E>> selectQuery;
  public final Check check;
  public final ObjectMapper om;
  public final EntityManager em;
  public final PlatformTransactionManager tm;
  public List<E> data;

  public static Collection<Integer> allIdxs(int size) {
    Collection<Integer> result = new ArrayList<>(size);
    for(Integer i = 0; i < size; ++i) {
      result.add(i);
    }
    return result;
  }

  public TestEntities(
    String title
  , String label
  , int size
  , Class<E> entityClass
  , BiFunction<String, Object, E> newEntity
  , Function<Iterable<K>, List<E>> selectQuery
  , Check check
  , ObjectMapper om
  , EntityManager em
  , PlatformTransactionManager tm
  ) {
    this.title = title;
    this.label = label;
    this.size = size;
    this.entityClass = entityClass;
    this.newEntity = newEntity;
    this.selectQuery = selectQuery;
    this.check = check;
    this.om = om;
    this.em = em;
    this.tm = tm;
    data = new ArrayList<>(size);
  }

  public Set<K> allIds() {
    Set<K> result = new HashSet<>();
    for(E d : data) {
      result.add(d.getId());
    }
    return result;
  }

  public Collection<Integer> allIdxs() {
    return allIdxs(size);
  }

  public void check(
    Iterable<Integer> indexes
  , IgnoringFields ignoringFields
  , String... fetchFields
  ) {
    em.clear();
    Iterable<K> ids = ids(indexes);
    List<E> res = new TransactionTemplate(tm).execute
    (status -> new SelectQuery<>(em, entityClass, fetchFields).apply(ids));
    assertThat(res).as(() -> "Expected data not found by ids=" + ids).isNotEmpty();
    String name = "actual";
    FilterProvider jsonFilter = ignoringFields.jsonFilter();
    log.info(() -> name + "=" + Json.serializep(om, res, jsonFilter));
    Iterable<E> expected = data(indexes);
    log.info(() -> "expected=" + Json.serializep(om, expected, jsonFilter));
    String[] ignoring = ArrayUtils.addAll(ignoringFields.names(), IGNORED_FIELDS);
    log.info(() -> "ignoring=" + Json.serializep(om, ignoring));
    check.data(name, res, expected, ignoring);
  }

  public Set<E> data(Iterable<Integer> indexes) {
    Set<E> result = null;
    if(indexes != null) {
      result = new HashSet<>();
      for(Integer i : indexes) {
        result.add(data.get(i));
      }
    }
    return result;
  }

  public Set<E> data(Integer... indexes) {
    Set<E> result = null;
    if(indexes != null) {
      result = new HashSet<>();
      for(Integer i : indexes) {
        result.add(data.get(i));
      }
    }
    return result;
  }

  public List<E> list(Iterable<Integer> indexes) {
    List<E> result = null;
    if(indexes != null) {
      result = new ArrayList<>();
      for(Integer i : indexes) {
        result.add(data.get(i));
      }
    }
    return result;
  }

  public List<E> list(Integer... indexes) {
    List<E> result = null;
    if(indexes != null) {
      result = new ArrayList<>();
      for(Integer i : indexes) {
        result.add(data.get(i));
      }
    }
    return result;
  }

  public List<K> idList(Iterable<Integer> indexes) {
    List<K> result = null;
    if(indexes != null) {
      result = new ArrayList<>();
      for(Integer i : indexes) {
        result.add(data.get(i).getId());
      }
    }
    return result;
  }

  public List<K> idList(Integer... indexes) {
    List<K> result = null;
    if(indexes != null) {
      result = new ArrayList<>();
      for(Integer i : indexes) {
        result.add(data.get(i).getId());
      }
    }
    return result;
  }

  public Set<K> ids(Iterable<Integer> indexes) {
    Set<K> result = null;
    if(indexes != null) {
      result = new HashSet<>();
      for(Integer i : indexes) {
        result.add(data.get(i).getId());
      }
    }
    return result;
  }

  public Set<K> ids(Integer... indexes) {
    Set<K> result = new HashSet<>();
    if(indexes != null) {
      for(Integer i : indexes) {
        result.add(data.get(i).getId());
      }
    }
    return result;
  }

  public void prePersist() {
    for(int i = 0; i < size; ++i) {
      data.add(newEntity.apply(label + entityClass.getSimpleName(), i));
    }
  }

  public void persist() {
    prePersist();
    new TransactionTemplate(tm).executeWithoutResult(status -> {
      for(E d : data) {
        em.persist(d);
      }
    });
    em.flush();
    em.clear();
  }

  public void read() {
    data = selectQuery.apply(Identifiable.idSet(data));
  }

  public void detach() {
    for(E d : data) {
      em.detach(d);
    }
  }

  public void preRemove(List<E> toRemove) {
  }

  public void remove() {
    new TransactionTemplate(tm).executeWithoutResult(status -> {
      List<E> toRemove = new SelectQuery<>(em, entityClass).apply(Identifiable.idSet(data));
      preRemove(toRemove);
      for(E item : toRemove) {
        em.remove(item);
      }
    });
    em.flush();
    em.clear();
  }

}
