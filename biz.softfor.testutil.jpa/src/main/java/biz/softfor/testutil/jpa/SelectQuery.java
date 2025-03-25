package biz.softfor.testutil.jpa;

import biz.softfor.util.StringUtil;
import biz.softfor.util.api.Identifiable;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.function.Function;
import org.apache.commons.lang3.StringUtils;

public class SelectQuery<K extends Number, E extends Identifiable<K>>
implements Function<Iterable<K>, List<E>> {

  private final EntityManager em;
  private final Class<E> entityClass;
  private final String[] fetchFields;

  private final String qry;

  public SelectQuery(EntityManager em, Class<E> entityClass, String... fetchFields) {
    this.em = em;
    this.entityClass = entityClass;
    this.fetchFields = fetchFields;

    String qf = "";
    for(String ff : fetchFields) {
      if(StringUtils.isNotBlank(ff)) {
        qf += " left join fetch a." + ff;
      }
    }
    qry = "select a from " + entityClass.getName() + " a" + qf + " where a." + Identifiable.ID + " in(";
  }

  @Override
  public List<E> apply(Iterable<K> ids) {
    String sIds = StringUtils.join(ids, ",");
    if(false) return em.createQuery(qry + sIds + ")", entityClass).getResultList();
    List<E> result = em.createQuery("select a from " + entityClass.getName() + " a where a." + Identifiable.ID + " in(" + sIds + ")", entityClass).getResultList();
    String joinFetch = "";
    for(int i = 0; i < fetchFields.length; ++i) {
      joinFetch += " left join fetch a." + fetchFields[i];
      if(i == fetchFields.length - 1 || !fetchFields[i + 1].startsWith(fetchFields[i] + StringUtil.FIELDS_DELIMITER)) {
        result = em.createQuery("select distinct a from " + entityClass.getName() + " a" + joinFetch + " where a in :p", entityClass)
        .setParameter("p", result).getResultList();
        joinFetch = "";
      }
    }
    return result;
  }

}
