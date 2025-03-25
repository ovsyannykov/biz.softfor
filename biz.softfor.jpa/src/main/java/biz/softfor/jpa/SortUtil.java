package biz.softfor.jpa;

import biz.softfor.jpa.crud.querygraph.ColumnDescr;
import biz.softfor.util.StringUtil;
import biz.softfor.util.api.ClientError;
import biz.softfor.util.api.Order;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.JoinType;
import java.beans.IntrospectionException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class SortUtil {

  public final static String THE_SORT_PROPERTY_IS_INVALID
  = "The sort property '{0}' is invalid.";

  public static <E> void orderBy
  (List<Order> sort, CriteriaQuery cq, From<E, E> from, CriteriaBuilder cb)
  throws IntrospectionException {
    if(sort != null && !sort.isEmpty()) {
      ArrayList<jakarta.persistence.criteria.Order> orderList = new ArrayList<>();
      Class<E> clazz = (Class<E>)from.getJavaType();
      for(Order s : sort) {
        Class<?> iClass = clazz;
        From iFrom = from;
        String sps[] = s.getProperty().split(StringUtil.FIELDS_DELIMITER_REGEX);
        for(int iSp = 0; iSp < sps.length; ++iSp) {
          String sp = sps[iSp];
          ColumnDescr cd = ColumnDescr.get(iClass).get(sp);
          if(cd.isPlain()) {
            if(iSp < sps.length - 1) {
              throw new ClientError(MessageFormat.format
              (THE_SORT_PROPERTY_IS_INVALID, s.getProperty()));
            }
          } else {
            iFrom = iFrom.join(sp, JoinType.LEFT);
          }
          iClass = cd.clazz;
        }
        Expression<?> expr = iFrom.get(sps[sps.length - 1]);
        jakarta.persistence.criteria.Order order;
        if(StringUtils.isBlank(s.getDirection())
        || s.getDirection().equalsIgnoreCase(Order.Direction.ASC)) {
          order = cb.asc(expr);
        } else if(s.getDirection().equalsIgnoreCase(Order.Direction.DESC)) {
          order = cb.desc(expr);
        } else {
          throw new IllegalArgumentException
          ("Sort direction '" + s.getDirection() + "' is not applicable.");
        }
        orderList.add(order);
      }
      cq.orderBy(orderList);
    }
  }

}
