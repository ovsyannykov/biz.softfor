package biz.softfor.util;

import biz.softfor.util.api.Order;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class SortDeserializer extends JsonDeserializer<List<Order>> {

  //for processing like this: orderBy = "DateDiff(s, IsNull(schedule_date, GETDATE()), GETDATE()) DESC, id ASC"
  public static boolean asIs = false;

  public static List<Order> parse(String v) {
    List<Order> result = new ArrayList<>();
    if(StringUtils.isNotBlank(v)) {
      if(asIs) {
        result.add(new Order(v, ""));
      } else {
        String[] orders = v.replaceAll("\\s+", " ").trim().split(",");
        for(String o : orders) {
          String[] e = o.split(" ");
          if(e.length > 0) {
            Order order = new Order(Order.Direction.ASC, e[0]);
            if(e.length > 1 && Order.Direction.DESC.equalsIgnoreCase(e[1])) {
              order.setDirection(Order.Direction.DESC);
            }
            result.add(order);
          }
        }
      }
    }
    return result;
  }

  @Override
  public List<Order> deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
    List<Order> result;
    ObjectCodec codec = parser.getCodec();
    JsonNode node = codec.readTree(parser);
    if(node.isArray()) {
      result = new ArrayList<>();
      for(JsonNode n : ((ArrayNode)node)) {
        Order o = codec.treeToValue(n, Order.class);
        result.add(o);
      }
    } else {
      String v = node.asText();
      result = parse(v);
    }
    return result;
  }

}
