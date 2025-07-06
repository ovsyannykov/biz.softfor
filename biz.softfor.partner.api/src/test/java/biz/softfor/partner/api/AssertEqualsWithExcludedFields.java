package biz.softfor.partner.api;

import lombok.extern.java.Log;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.list;
import org.junit.jupiter.api.Test;

@Log
public class AssertEqualsWithExcludedFields {

  @Test
  public void AssertEqualsWithExcludedFields() {
    PartnerRto partnerExpected = new PartnerRto();
    partnerExpected.setId(1L);
    partnerExpected.setPartnerName("nameExpected");
    partnerExpected.setContactIds(list(10L, 11L));
    PartnerRto partnerActual = new PartnerRto();
    partnerActual.setPartnerName(partnerExpected.getPartnerName());
    partnerActual.setContactIds(list(11L, 10L));
    assertThat(partnerActual)
    .usingRecursiveComparison()
    .ignoringCollectionOrder()
    .ignoringFields(PartnerRto.ID)
    .as(() -> "Expected differ than actual")
    .isEqualTo(partnerExpected);
  }

}
