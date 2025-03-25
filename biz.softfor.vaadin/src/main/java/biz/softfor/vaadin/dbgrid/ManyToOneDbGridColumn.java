package biz.softfor.vaadin.dbgrid;

import biz.softfor.util.api.Identifiable;
import biz.softfor.util.api.filter.FilterId;
import com.vaadin.flow.data.renderer.Renderer;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ManyToOneDbGridColumn
<M, MF extends FilterId, K extends Number, E extends Identifiable<K>>
extends DbGridColumn<M, Set<K>, ManyToOneGridColumnComponent<K, E>, Set<E>, MF> {

  public static <K extends Number, E extends Identifiable<K>, F extends FilterId>
  BiConsumer<F, ManyToOneGridColumnComponent<K, E>> defaultFilter
  (BiConsumer<F, Set<K>> setter) {
    return (requestFilter, component) -> {
      Set<E> v = component.getValue();
      if(v != null && !v.isEmpty()) {
        setter.accept(requestFilter, Identifiable.ids(v));
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
      (dbName, dbGrid, view, detail, involvedFields).configure()
    , renderer
    , filter
    );
  }

  @Override
  public List<String> involvedFields() {
    return component.involvedFields();
  }

}
