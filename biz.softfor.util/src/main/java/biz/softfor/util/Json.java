package biz.softfor.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.apache.commons.lang3.StringUtils;

public class Json {

  public final static String UNSERIALIZABLE_VALUE = "'Unserializable value': ";
  public final static TypeReference<Map<String, Object>>
  MAP_STRING_OBJECT_TYPEREF = new TypeReference<Map<String, Object>>() {};

  public static boolean isJson(String s, ObjectMapper objectMapper) {
    boolean result = false;
    if(StringUtils.isNotBlank(s)) {
      try {
        objectMapper.readTree(s);
        result = true;
      }
      catch(IOException ex) {
      }
    }
    return result;
  }

  public static JsonNode getCI(JsonNode arrayNode, String field) {
    JsonNode result = null;
    Iterator<String> it = arrayNode.fieldNames();
    while(it.hasNext()) {
      String name = it.next();
      if(field.equalsIgnoreCase(name)) {
        result = arrayNode.get(name);
        break;
      }
    }
    return result;
  }

  public static ObjectMapper objectMapper() {
    //https://github.com/FasterXML/jackson-databind/wiki
    ObjectMapper result = JsonMapper.builder()
    .addModule(new Jdk8Module())
    .addModule(new JavaTimeModule())
    .enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES)//spring.jackson.parser.allow-single-quotes=true
    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)//spring.jackson.deserialization.fail-on-unknown-properties=false
    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)//spring.jackson.serialization.write-dates-as-timestamps=false
    .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)//spring.jackson.mapper.accept_case_insensitive_properties=true

    //.enable(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER)//! This skip backslashes.
    //.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
    //.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
    //.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING)

    .build()

    .setSerializationInclusion(Include.NON_NULL)//spring.jackson.default-property-inclusion=non_null

    .setFilterProvider(new SimpleFilterProvider().setFailOnUnknownId(false))

    //spring.jackson.date-format
    //Date format string or a fully-qualified date format class name. For instance, 'yyyy-MM-dd HH:mm:ss'.
    .setDateFormat(new StdDateFormat().withTimeZone(TimeZone.getTimeZone("Europe/Kiev")))
    ;
    //DefaultSerializerProvider sp = new DefaultSerializerProvider.Impl();
    //sp.setNullValueSerializer(new Json.NullSerializer());
    //result.setSerializerProvider(sp);

    return result;
  }

  public static String serialize(ObjectMapper objectMapper, Object v) {
    String result;
    try {
      result = objectMapper.writeValueAsString(v);
    }
    catch(JsonProcessingException ex) {
      result = UNSERIALIZABLE_VALUE + ex.getMessage();
    }
    return result;
  }

  public static String serialize
  (ObjectMapper objectMapper, Object v, FilterProvider filter)
  throws JsonProcessingException {
    if(filter == null) {
      filter = new SimpleFilterProvider().setFailOnUnknownId(false);
    }
    return objectMapper.writer(filter).writeValueAsString(v);
  }

  public static String serializep(ObjectMapper objectMapper, Object v) {
    String result;
    try {
      result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(v);
    }
    catch(JsonProcessingException ex) {
      result = UNSERIALIZABLE_VALUE + ex.getMessage();
    }
    return result;
  }

  public static String serializep(ObjectMapper objectMapper, Object v, FilterProvider filter) {
    String result;
    try {
      if(filter == null) {
        filter = new SimpleFilterProvider().setFailOnUnknownId(false);
      }
      result = objectMapper.writer(filter).withDefaultPrettyPrinter().writeValueAsString(v);
    }
    catch(JsonProcessingException ex) {
      result = UNSERIALIZABLE_VALUE + ex.getMessage();
    }
    return result;
  }

  public static class BooleanDeserializer extends JsonDeserializer<Boolean> {

    @Override
    public Boolean deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
      String v = parser.getText().toLowerCase();
      return !("false".equals(v) || "0".equals(v) || "f".equals(v) || "n".equals(v) || "no".equals(v));
    }

  }

  public static class Empty2DefaultIntegerDeserializer extends JsonDeserializer<Integer> {

    protected final static Integer V = 0;//null not available value by design (((

    @Override
    public Integer deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
      JsonNode node = parser.readValueAsTree();
      return node.asText().isEmpty() ? V : node.asInt();
    }

  }

  public static class IntegerListDeserializer extends ListDeserializer<Integer> {

    @Override
    public Integer parse(String value, JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
      return Integer.parseInt(value);
    }

  }

  public static class NullSerializer extends JsonSerializer<Object> {

    @Override
    public void serialize
    (Object value, JsonGenerator gen, SerializerProvider provider)
    throws IOException, JsonProcessingException {
      gen.writeString("");
    }

  }

  public static abstract class ListDeserializer<T>
  extends JsonDeserializer<List<T>> {

    @Override
    public List<T> deserialize(JsonParser parser, DeserializationContext context)
    throws IOException, JsonProcessingException {
      List<T> result = new ArrayList<>();
      String value = parser.getText();
      if(!StringUtils.isBlank(value)) {
        String[] values = value.split(",");
        for(String v : values) {
          result.add(parse(v, parser, context));
        }
      }
      return result;
    }

    public abstract T parse
    (String value, JsonParser parser, DeserializationContext context)
    throws IOException, JsonProcessingException;

  }

  public static class StringListDeserializer extends ListDeserializer<String> {

    @Override
    public String parse
    (String value, JsonParser parser, DeserializationContext context)
    throws IOException, JsonProcessingException {
      return value;
    }

  }

}
