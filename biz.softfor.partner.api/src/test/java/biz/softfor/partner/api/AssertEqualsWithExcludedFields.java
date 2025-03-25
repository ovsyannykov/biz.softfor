package biz.softfor.partner.api;

import java.util.Set;
import lombok.extern.java.Log;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

@Log
public class AssertEqualsWithExcludedFields {

  @Test
  public void AssertEqualsWithExcludedFields() {
    PartnerDto partnerExpected = new PartnerDto();
    partnerExpected.setId(1L);
    partnerExpected.setPartnerName("nameExpected");
    partnerExpected.setContactIds(Set.of(10L, 11L));
    PartnerDto partnerActual = new PartnerDto();
    partnerActual.setPartnerName(partnerExpected.getPartnerName());
    partnerActual.setContactIds(Set.of(11L, 10L));
    assertThat(partnerActual)
    .usingRecursiveComparison()
    .ignoringCollectionOrder()
    .ignoringFields(PartnerDto.ID)
    .as(() -> "Expected differ than actual")
    .isEqualTo(partnerExpected);
  }

}
