package biz.softfor.util;

import biz.softfor.util.api.BasicResponse;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public class FieldsList {

  @SuppressWarnings("unchecked")
  public static List<String> parse(Object fields) {
    List<String> result;
    if(fields instanceof List fieldsAsList) {
      result = fieldsAsList;
    } else {
      result = new ArrayList<>();
      if(fields != null && !"".equals(fields)) {
        if(fields instanceof String fieldsAsString) {
          String[] fieldsArray = fieldsAsString.split(",");
          for(String field : fieldsArray) {
            String f = field.trim();
            if(!f.isEmpty()) {
              result.add(f);
            }
          }
        } else {
          throw new AbstractError(
            "The parameter 'fieldsList' has bad format"
          , BasicResponse.REQUEST_PARSE
          );
        }
      }
    }
    return result;
  }

  public static class Deserializer extends JsonDeserializer<List<String>> {

    @Override
    public List<String> deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
      List<String> result = new ArrayList<>();
      String fields = parser.getText();
      if(fields != null && !"".equals(fields)) {
        for(String field : fields.split(",")) {
          String f = field.trim();
          if(!f.isEmpty()) {
            result.add(f);
          }
        }
      }
      return result;
    }

  }

  public static List<String> fetchByPrefix(List<String> fields, String prefix) {
    List<String> result = new ArrayList<>();
    int prefixLen = prefix.length();
    fields.forEach(f -> {
      if(StringUtils.startsWithIgnoreCase(f, prefix)) {
        f = f.substring(prefixLen);
        if(!f.isEmpty()) {
          result.add(f);
        }
      }
    });
    return result;
  }

  public static Set<String> filter(List<String> fields, Set<String> properties) {
    Set<String> result = new HashSet<>();
    for(String f : fields) {
      for(String p : properties) {
        if(f.equalsIgnoreCase(p)) {
          result.add(p);
          break;
        }
      }
    }
    return result;
  }

}
