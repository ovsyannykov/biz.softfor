package biz.softfor.vaadin.field.grid;

import biz.softfor.vaadin.GridColumn;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.data.renderer.Renderer;
import java.util.function.BiFunction;

public class GridFieldColumn<E, V, C extends AbstractField, CV>
extends GridColumn<E, C> {

  private final BiFunction<CV, E, Boolean> filter;

  private CV value;
  private GridListDataView<E> dataView;

  public GridFieldColumn(
    String dbName
  , C component
  , Renderer<E> renderer
  , BiFunction<CV, E, Boolean> filter
  ) {
    super(dbName, component, renderer);
    this.filter = filter;
    component.addValueChangeListener(e -> {
      value = (CV)e.getValue();
      dataView.refreshAll();
    });
  }

  public final boolean filter(E item) {
    return filter.apply(value, item);
  }

  public final void setDataView(GridListDataView<E> dataView) {
    this.dataView = dataView;
  }

}
