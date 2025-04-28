package biz.softfor.vaadin.partner;

import biz.softfor.partner.api.PartnerFltr;
import biz.softfor.partner.jpa.Partner;
import biz.softfor.partner.jpa.Partner_;
import biz.softfor.partner.jpa.PersonDetails_;
import biz.softfor.util.StringUtil;
import biz.softfor.util.api.filter.FilterId;
import biz.softfor.vaadin.dbgrid.ManyToOneDbGridColumn;
import static biz.softfor.vaadin.dbgrid.ManyToOneDbGridColumn.defaultFilter;
import biz.softfor.vaadin.VaadinUtil;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class PartnerBasicDbGridColumn<M, MF extends FilterId>
extends ManyToOneDbGridColumn<M, MF, Long, Partner> {

  public PartnerBasicDbGridColumn(
    String dbName
  , Function<M, Partner> getter
  , Function<MF, PartnerFltr> filterGetter
  , BiConsumer<MF, PartnerFltr> filterSetter
  , Supplier<PartnerFltr> filterSupplier
  , PartnersBasicDbGrid partners
  ) {
    super(
      dbName
    , VaadinUtil.defaultRenderer(m -> {
        Partner e = getter.apply(m);
        return e == null ? "" : e.label();
      })
      , defaultFilter(filterGetter, filterSetter, filterSupplier)
      , partners
      , Partner::label
      , Partner::details
      , List.of(
          Partner_.TYP
        , Partner_.PARTNER_NAME
        , Partner_.PARTNER_FULLNAME
        , StringUtil.field(Partner_.PERSON_DETAILS, PersonDetails_.MIDDLENAME)
        )
    );
  }

}
