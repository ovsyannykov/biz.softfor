package biz.softfor.vaadin.partner;

import biz.softfor.address.api.PostcodeFltr;
import biz.softfor.address.jpa.Postcode;
import biz.softfor.address.jpa.Postcode_;
import biz.softfor.partner.api.LocationTypeFltr;
import biz.softfor.partner.api.PartnerDetailsFltr;
import biz.softfor.partner.api.PartnerFltr;
import biz.softfor.partner.api.PersonDetailsFltr;
import biz.softfor.partner.jpa.LocationType;
import biz.softfor.partner.jpa.LocationType_;
import biz.softfor.partner.jpa.Partner;
import biz.softfor.partner.jpa.PartnerDetails;
import biz.softfor.partner.jpa.PartnerDetails_;
import biz.softfor.partner.jpa.Partner_;
import biz.softfor.partner.jpa.PersonDetails;
import biz.softfor.partner.jpa.PersonDetails_;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.util.StringUtil;
import biz.softfor.util.api.Order;
import biz.softfor.util.api.filter.FilterId;
import biz.softfor.util.partner.PartnerType;
import biz.softfor.vaadin.VaadinUtil;
import biz.softfor.vaadin.address.PostcodesDbGrid;
import biz.softfor.vaadin.dbgrid.ComboBoxDbGridColumn;
import biz.softfor.vaadin.dbgrid.DateDbGridColumn;
import biz.softfor.vaadin.dbgrid.DbGridColumn;
import biz.softfor.vaadin.dbgrid.DbGridColumns;
import biz.softfor.vaadin.dbgrid.ManyToOneDbGridColumn;
import biz.softfor.vaadin.dbgrid.TextDbGridColumn;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PartnerDbGridColumns extends DbGridColumns<Long, Partner> {

  public final static String TITLE = "partners";
  public final static List<Order> DEFAULT_SORT
  = List.of(new Order(Order.Direction.ASC, Partner_.PARTNER_NAME));

  public static DbGridColumn
  <Partner, ?, ? extends AbstractField, ?, ? extends FilterId<Long>>[] columns
  (PostcodesDbGrid postcodesDbGrid, LocationTypesDbGrid locationTypesDbGrid) {
    return new DbGridColumn[] {
      new ComboBoxDbGridColumn<>
      (Partner_.TYP, PartnerType.VALUES, Partner::getTyp, PartnerFltr::setTyp)
    , new TextDbGridColumn<>(Partner_.PARTNER_NAME, PartnerFltr::setPartnerName)
    , new TextDbGridColumn<>
      (Partner_.PARTNER_FULLNAME, PartnerFltr::setPartnerFullname)
    , new DateDbGridColumn<>(
        Partner_.PARTNER_REGDATE
      , Partner::getPartnerRegdate
      , PartnerFltr::setPartnerRegdate
      )
    , new TextDbGridColumn<>
      (Partner_.PARTNER_REGCODE, PartnerFltr::setPartnerRegcode)
    , new TextDbGridColumn<>(Partner_.ADDRESS, PartnerFltr::setAddress)
    , new ManyToOneDbGridColumn<Partner, PartnerFltr, Integer, Postcode>(
        Partner_.POSTCODE
      , VaadinUtil.defaultRenderer(m -> {
          Postcode e = m.getPostcode();
          return e == null ? "" : e.getPostcode();
        })
      , ManyToOneDbGridColumn.defaultFilter
        (PartnerFltr::getPostcode, PartnerFltr::setPostcode, PostcodeFltr::new)
      , postcodesDbGrid
      , Postcode::getPostcode
      , Postcode::getPostcode
      , List.of(Postcode_.POSTCODE)
      )
    , new ManyToOneDbGridColumn<Partner, PartnerFltr, Short, LocationType>(
        Partner_.LOCATION_TYPE
      , VaadinUtil.defaultRenderer(m -> {
          LocationType c = m.getLocationType();
          return c == null ? "" : c.getName();
        })
      , ManyToOneDbGridColumn.defaultFilter(
          PartnerFltr::getLocationType
        , PartnerFltr::setLocationType
        , LocationTypeFltr::new
        )
      , locationTypesDbGrid
      , LocationType::getName
      , LocationType::getDescr
      , List.of(LocationType_.NAME, LocationType_.DESCR)
      )
    };
  }

  private static Renderer<Partner> personRenderer
  (Function<PersonDetails, String> label) {
    return VaadinUtil.defaultRenderer(p -> {
      String result = "";
      PersonDetails perDet = p.getPersonDetails();
      if(perDet != null) {
        result = label.apply(perDet);
      }
      return result;
    });
  }

  private static BiConsumer<PartnerFltr, TextField> personFilter
  (BiConsumer<PersonDetailsFltr, String> setter) {
    return (filter, component) -> {
      String v = component.getValue();
      if(StringUtils.isNotBlank(v)) {
        PersonDetailsFltr perDet = filter.getPersonDetails();
        if(perDet == null) {
          perDet = new PersonDetailsFltr();
          filter.setPersonDetails(perDet);
        }
        setter.accept(perDet, "%" + v + "%");
      }
    };
  }

  public PartnerDbGridColumns(
    SecurityMgr securityMgr
  , PostcodesDbGrid postcodes
  , LocationTypesDbGrid locationTypes
  , PartnersBasicDbGrid parents
  ) {
    super(
      Partner.TABLE
    , securityMgr
    , Partner.class
    , ArrayUtils.addAll(
        columns(postcodes, locationTypes)
      , new PartnerBasicDbGridColumn<>(
          Partner_.PARENT
        , Partner::getParent
        , PartnerFltr::getParent
        , PartnerFltr::setParent
        , PartnerFltr::new
        , parents
        )
      , new TextDbGridColumn<>(
          StringUtil.field(Partner_.PARTNER_DETAILS, PartnerDetails_.NOTE)
        , VaadinUtil.defaultRenderer(e -> {
          PartnerDetails partnerDet = e.getPartnerDetails();
          return partnerDet == null ? "" : partnerDet.getNote();
        })
        , TextDbGridColumn.defaultFilter((PartnerFltr f, String v) -> {
            PartnerDetailsFltr partnerDet = f.getPartnerDetails();
            if(partnerDet == null) {
              partnerDet = new PartnerDetailsFltr();
              f.setPartnerDetails(partnerDet);
            }
            partnerDet.setNote(v);
          })
        )
      , new TextDbGridColumn<>(
          StringUtil.field(Partner_.PERSON_DETAILS, PersonDetails_.MIDDLENAME)
        , personRenderer(PersonDetails::getMiddlename)
        , personFilter(PersonDetailsFltr::setMiddlename)
        )
      )
    );
  }

  @Override
  public List<Order> sort() {
    return DEFAULT_SORT;
  }

}
