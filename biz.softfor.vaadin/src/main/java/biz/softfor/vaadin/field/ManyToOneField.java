package biz.softfor.vaadin.field;

import biz.softfor.util.api.Identifiable;
import biz.softfor.util.api.ReadRequest;
import biz.softfor.util.api.filter.FilterId;
import biz.softfor.vaadin.dbgrid.DbGrid;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.combobox.ComboBox;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ManyToOneField
<K extends Number, E extends Identifiable<K>, F extends FilterId<K>>
extends ManyToOneBasicField<K, E, F, E, ComboBox<E>> {

  public ManyToOneField(
    String name
  , DbGrid<K, E, ? extends Identifiable<K>, F> dbGrid
  , ItemLabelGenerator<E> label
  , Function<E, String> detail
  , List<String> involvedFields
  , BiConsumer<ReadRequest<K, F>, String> fillRequest
  ) {
    super(
      name
    , dbGrid
    , label
    , detail
    , involvedFields
    , fillRequest
    , new ComboBox<>()
    , dbg -> dbg.grid.asSingleSelect().getValue()
    , (dbg, v) -> dbg.grid.asSingleSelect().setValue(v)
    , detail
    );
  }

}
