package biz.softfor.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.ToString;

@ToString
public enum BooleansEnum implements Labeled {

    FALSE((byte)0, BooleansEnum.No, Boolean.FALSE)
  , TRUE((byte)1, BooleansEnum.Yes, Boolean.TRUE)
  , UNDEFINED((byte)2, "", null)
  ;

  public final byte id;
  public final String label;
  public final Boolean value;

  public final static String No = "No";
  public final static String Yes = "Yes";
  public final static BooleansEnum[] DEFINED_VALUES = { FALSE, TRUE };
  public final static BooleansEnum[] VALUES = { FALSE, TRUE, UNDEFINED };

  @JsonCreator
  public static BooleansEnum of(Byte v) {
    BooleansEnum result = null;
    if(v != null) {
      byte value = v;
      for(BooleansEnum t : VALUES) {
        if(t.id == value) {
          result = t;
          break;
        }
      }
    }
    return result;
  }

  public static BooleansEnum of(Boolean v) {
    BooleansEnum result;
    if(v == null) {
      result = UNDEFINED;
    } else {
      result = v ? TRUE : FALSE;
    }
    return result;
  }

  private BooleansEnum(byte id, String label, Boolean value) {
    this.id = id;
    this.label = label;
    this.value = value;
  }

  @Override
  public String label() {
    return label;
  }

}
