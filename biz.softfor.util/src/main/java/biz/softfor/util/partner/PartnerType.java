package biz.softfor.util.partner;

import biz.softfor.util.Labeled;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.IOException;
import lombok.ToString;

@ToString
@JsonSerialize(using = PartnerType.Serializer.class)
public enum PartnerType implements Labeled {

    PERSON((short)0, "Person", true)
  , EMPLOYEE((short)10, "Employee", true)
  , DEPARTMENT((short)20, "Department", false)
  , LEGAL_ENTITY((short)30, "Legal_entity", false)
  , ORGANIZATION((short)31, "Organization", false)
  ;

  public final short id;
  public final String label;
  public final boolean isPerson;

  public final static PartnerType[] VALUES = values();

  @JsonCreator
  public static PartnerType of(Short v) {
    PartnerType result = null;
    if(v != null) {
      short value = v;
      for(PartnerType t : VALUES) {
        if(t.id == value) {
          result = t;
          break;
        }
      }
    }
    return result;
  }

  private PartnerType(short id, String label, boolean isPerson) {
    this.id = id;
    this.label = label;
    this.isPerson = isPerson;
  }

  @Override
  public String label() {
    return label;
  }

  public static class Serializer extends JsonSerializer<PartnerType> {

    @Override
    public void serialize
    (PartnerType value, JsonGenerator gen, SerializerProvider prov)
    throws IOException {
      gen.writeNumber(value.id);
    }

    @Override
    public Class<PartnerType> handledType() {
      return PartnerType.class;
    }

  }

}
