package biz.softfor.jpa;

import biz.softfor.util.security.DefaultAccess;
import jakarta.persistence.AttributeConverter;

public class DefaultAccessConv
implements AttributeConverter<DefaultAccess, Byte> {

  @Override
  public Byte convertToDatabaseColumn(DefaultAccess v) {
    return v == null ? null : v.id;
  }

  @Override
  public DefaultAccess convertToEntityAttribute(Byte v) {
    return DefaultAccess.of(v);
  }

}
