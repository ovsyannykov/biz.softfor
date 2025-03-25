package biz.softfor.util.security;

import biz.softfor.util.Labeled;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.IOException;
import lombok.ToString;

@ToString
@JsonSerialize(using = DefaultAccess.Serializer.class)
public enum DefaultAccess implements Labeled {

    EVERYBODY((byte)0, "Everybody")
  , AUTHORIZED((byte)1, "Authorized")
  , NOBODY((byte)2, "Nobody")
  ;

  public final byte id;
  public final String label;

  public final static DefaultAccess[] VALUES = values();

  @JsonCreator
  public static DefaultAccess of(Byte v) {
    DefaultAccess result = null;
    if(v != null) {
      byte value = v;
      for(DefaultAccess t : VALUES) {
        if(t.id == value) {
          result = t;
          break;
        }
      }
    }
    return result;
  }

  private DefaultAccess(byte id, String label) {
    this.id = id;
    this.label = label;
  }

  @Override
  public String label() {
    return label;
  }

  public static class Serializer extends JsonSerializer<DefaultAccess> {

    @Override
    public void serialize
    (DefaultAccess value, JsonGenerator gen, SerializerProvider prov)
    throws IOException {
      gen.writeNumber(value.id);
    }

    @Override
    public Class<DefaultAccess> handledType() {
      return DefaultAccess.class;
    }

  }

}
