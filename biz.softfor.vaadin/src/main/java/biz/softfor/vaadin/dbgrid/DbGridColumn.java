package biz.softfor.vaadin.dbgrid;

import biz.softfor.util.api.filter.FilterId;
import biz.softfor.vaadin.GridColumn;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.data.renderer.Renderer;
import java.util.function.BiConsumer;

public class DbGridColumn<E, V, C extends AbstractField, CV, F extends FilterId>
extends GridColumn<E, C> {

  public final BiConsumer<F, C> filter;

  protected DbGridColumn
  (String dbName, C component, Renderer<E> renderer, BiConsumer<F, C> filter) {
    super(dbName, component, renderer);
    this.filter = filter;
  }

}
