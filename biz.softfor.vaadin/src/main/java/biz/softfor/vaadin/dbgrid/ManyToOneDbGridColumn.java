package biz.softfor.vaadin.dbgrid;

import biz.softfor.util.api.Identifiable;
import biz.softfor.util.api.filter.FilterId;
import com.vaadin.flow.data.renderer.Renderer;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ManyToOneDbGridColumn
<M, MF extends FilterId, K extends Number, E extends Identifiable<K>>
extends DbGridColumn<M, Set<K>, ManyToOneGridColumnComponent<K, E>, Set<E>, MF> {

  public static <
    K extends Number
  , E extends Identifiable<K>
  , F extends FilterId
  , VF extends FilterId
  > BiConsumer<F, ManyToOneGridColumnComponent<K, E>> defaultFilter
  (Function<F, VF> getter, BiConsumer<F, VF> setter, Supplier<VF> supplier) {
    return (requestFilter, component) -> {
      Set<E> v = component.getValue();
      if(v != null && !v.isEmpty()) {
        VF vf = getter.apply(requestFilter);
        if(vf == null) {
          vf = supplier.get();
          setter.accept(requestFilter, vf);
        }
        vf.setId(Identifiable.ids(v));
      }
    };
  }

  public ManyToOneDbGridColumn(
    String dbName
  , Renderer<M> renderer
  , BiConsumer<MF, ManyToOneGridColumnComponent<K, E>> filter
  , DbGrid<K, E, ? extends Identifiable<K>> dbGrid
  , Function<E, String> view
  , Function<E, String> detail
  , List<String> involvedFields
  ) {
    super(
      dbName
    , new ManyToOneGridColumnComponent<>
      (dbName, dbGrid, view, detail, involvedFields)
    , renderer
    , filter
    );
  }

  @Override
  public List<String> involvedFields() {
    return component.involvedFields();
  }

}
