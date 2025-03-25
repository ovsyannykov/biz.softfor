package biz.softfor.vaadin.partner;

import biz.softfor.partner.jpa.Partner;
import biz.softfor.partner.jpa.Partner_;
import biz.softfor.partner.jpa.PersonDetails_;
import biz.softfor.util.StringUtil;
import biz.softfor.util.api.filter.FilterId;
import biz.softfor.vaadin.dbgrid.ManyToOneDbGridColumn;
import biz.softfor.vaadin.VaadinUtil;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class PartnerBasicDbGridColumn<M, MF extends FilterId>
extends ManyToOneDbGridColumn<M, MF, Long, Partner> {

  public PartnerBasicDbGridColumn(
    String dbName
  , Function<M, Partner> getter
  , BiConsumer<MF, Set<Long>> filterSetter
  , PartnersBasicDbGrid partners
  ) {
    super(
      dbName
    , VaadinUtil.defaultRenderer(m -> {
        Partner e = getter.apply(m);
        return e == null ? "" : e.label();
      })
      , ManyToOneDbGridColumn.defaultFilter(filterSetter)
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
