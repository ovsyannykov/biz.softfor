package biz.softfor.partner.jpa;

import biz.softfor.util.partner.PartnerType;
import jakarta.persistence.AttributeConverter;

public class PartnerTypeConverter
implements AttributeConverter<PartnerType, Short> {

  @Override
  public Short convertToDatabaseColumn(PartnerType v) {
    return v == null ? null : v.id;
  }

  @Override
  public PartnerType convertToEntityAttribute(Short v) {
    return PartnerType.of(v);
  }

}
