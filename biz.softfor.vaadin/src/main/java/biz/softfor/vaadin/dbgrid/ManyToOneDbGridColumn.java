package biz.softfor.vaadin.dbgrid;

import biz.softfor.util.api.Identifiable;
import biz.softfor.util.api.ReadRequest;
import biz.softfor.util.api.filter.FilterId;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.data.renderer.Renderer;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ManyToOneDbGridColumn<
  M
, MF extends FilterId
, K extends Number
, E extends Identifiable<K>
, F extends FilterId<K>
> extends DbGridColumn
<M, Set<K>, ManyToOneGridColumnComponent<K, E, F>, Set<E>, MF> {

  public static <
    K extends Number
  , E extends Identifiable<K>
  , F extends FilterId<K>
  , MF extends FilterId
  > BiConsumer<MF, ManyToOneGridColumnComponent<K, E, F>> defaultFilter
  (Function<MF, F> getter, BiConsumer<MF, F> setter, Supplier<F> supplier) {
    return (requestFilter, component) -> {
      Set<E> v = component.getValue();
      if(v != null && !v.isEmpty()) {
        F f = getter.apply(requestFilter);
        if(f == null) {
          f = supplier.get();
          setter.accept(requestFilter, f);
        }
        f.setId(Identifiable.ids(v));
      }
    };
  }

  public ManyToOneDbGridColumn(
    String dbName
  , Renderer<M> renderer
  , BiConsumer<MF, ManyToOneGridColumnComponent<K, E, F>> filter
  , DbGrid<K, E, ? extends Identifiable<K>, F> dbGrid
  , ItemLabelGenerator<E> view
  , Function<E, String> detail
  , List<String> involvedFields
  , BiConsumer<ReadRequest<K, F>, String> fillRequest
  ) {
    super(
      dbName
    , new ManyToOneGridColumnComponent<>
      (dbName, dbGrid, view, detail, involvedFields, fillRequest)
    , renderer
    , filter
    );
  }

  @Override
  public List<String> involvedFields() {
    return component.involvedFields();
  }

}
