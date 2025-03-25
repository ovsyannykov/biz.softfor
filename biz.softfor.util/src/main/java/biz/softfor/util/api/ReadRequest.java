package biz.softfor.util.api;

import biz.softfor.util.SortDeserializer;
import biz.softfor.util.api.filter.FilterId;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ReadRequest<K extends Number, F extends FilterId<K>>
extends DeleteRequest<K, F> {

  @JsonDeserialize(using = SortDeserializer.class)//receives comma-separated list of ORDER BY clauses
  public List<Order> sort;
  public final static String SORT = "sort";

  //@JsonDeserialize(using = FieldsList.Deserializer.class)//receives comma-separated list of field names
  public List<String> fields;

  public ReadRequest() {
    sort = new ArrayList<>();
    sort.add(new Order(Order.Direction.ASC, Identifiable.ID));
  }

}
