package biz.softfor.vaadin.field.grid;

import biz.softfor.util.StringUtil;
import biz.softfor.util.api.Identifiable;
import biz.softfor.vaadin.CSS;
import biz.softfor.vaadin.DbNamedColumn;
import biz.softfor.vaadin.GridColumn;
import biz.softfor.vaadin.VaadinUtil;
import com.vaadin.flow.component.AbstractCompositeField;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class GridField
<K extends Number, E extends Identifiable<K>, M, V extends Collection<E>>
extends AbstractCompositeField<VerticalLayout, GridField<K, E, M, V>, V>
implements DbNamedColumn, LocaleChangeObserver {

  private final String dbName;
  protected final Class<E> clazz;
  protected final Supplier<V> emptyValue;
  public final GridFieldColumns<K, E> columns;
  private final Span title;
  protected final HorizontalLayout toolbar;
  public final Grid<E> grid;
  private final List<String> involvedFields;

  public GridField(
    String dbName
  , Class<E> clazz
  , Supplier<V> emptyValue
  , GridFieldColumns<K, E> columns
  ) {
    super(emptyValue.get());
    this.dbName = dbName;
    this.clazz = clazz;
    this.emptyValue = emptyValue;
    this.columns = columns;
    getContent().addClassName(CSS.GRID_VIEW);
    grid = GridColumn.grid(this.clazz, this.columns, this.columns.sort());
    title = VaadinUtil.label(getTranslation(StringUtil.fieldToName(this.dbName)));
    toolbar = new HorizontalLayout(title);
    toolbar.setAlignItems(FlexComponent.Alignment.CENTER);
    getContent().add(toolbar, grid);
    involvedFields = new ArrayList<>();
    for(GridFieldColumn c : this.columns) {
      involvedFields.add(c.dbName());
    }
  }

  @Override
  public String dbName() {
    return dbName;
  }

  public boolean filter(E v) {
    boolean result = true;
    for(GridColumn c : columns) {
      if(!((GridFieldColumn)c).filter(v)) {
        result = false;
        break;
      }
    }
    return result;
  }

  @Override
  public List<String> involvedFields() {
    return involvedFields;
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    title.setText(getTranslation(StringUtil.fieldToName(dbName)));
    GridColumn.localeChangeColumns(columns);
  }

  @Override
  public V getEmptyValue() {
    return emptyValue.get();
  }

  @Override
  protected void setPresentationValue(V v) {
    GridListDataView<E> dataView = grid.setItems(v);
    dataView.addFilter(this::filter);
    for(GridColumn c : columns) {
      ((GridFieldColumn)c).setDataView(dataView);
    }
  }

}
