package biz.softfor.partner.jpa;

public class PartnerWorValidation {

  public static boolean isPartnerTypeIsPersonOrPersonDetailsIsEmpty(PartnerWor v) {
    return v.getTyp() != null && v.getTyp().isPerson || v.getPersonDetails() == null;
  }

}
