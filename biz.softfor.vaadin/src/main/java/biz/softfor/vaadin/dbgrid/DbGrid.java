package biz.softfor.vaadin.dbgrid;

import biz.softfor.jpa.crud.AbstractCrudSvc;
import biz.softfor.jpa.crud.querygraph.ColumnDescr;
import biz.softfor.jpa.crud.querygraph.EntityInf;
import biz.softfor.spring.jpa.crud.CrudSvc;
import biz.softfor.util.Reflection;
import biz.softfor.util.api.Identifiable;
import biz.softfor.util.api.ReadRequest;
import biz.softfor.util.api.filter.FilterId;
import biz.softfor.vaadin.CSS;
import biz.softfor.vaadin.DbNamedColumn;
import biz.softfor.vaadin.GridColumn;
import biz.softfor.vaadin.Text;
import biz.softfor.vaadin.VaadinUtil;
import com.vaadin.flow.component.HasLabel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridLazyDataView;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import java.util.ArrayList;
import java.util.function.Consumer;

public class DbGrid
<K extends Number, E extends Identifiable<K>, WOR extends Identifiable<K>>
extends VerticalLayout implements LocaleChangeObserver {

  public final EntityInf entityInf;
  public final CrudSvc<K, E, WOR, ?> service;
  public final DbGridColumns<K, E> columns;
  private final DbGridColumns<K, E> filters;
  public final ReadRequest readRequest;
  public final Grid<E> grid;
  public final HorizontalLayout toolbar;

  public GridLazyDataView<E> dataView;
  private Consumer filter;

  private final Span title;
  private final Button filtrate;
  private final Button clear;

  public static String clearId(Class<?> clazz) {
    return Text.Clear + "-" + clazz.getSimpleName() + VaadinUtil.GRID_ID_OBJ;
  }

  public static String filtrateId(Class<?> clazz) {
    return Text.Filtrate + "-" + clazz.getSimpleName() + VaadinUtil.GRID_ID_OBJ;
  }

  public DbGrid
  (Class<E> clazz, DbGridColumns<K, E> columns, DbGridColumns<K, E> filters) {
    entityInf = ColumnDescr.getInf(clazz);
    service = (CrudSvc<K, E, WOR, ?>)AbstractCrudSvc.service(clazz);
    this.columns = columns;
    this.filters = filters;

    readRequest = Reflection.newInstance(entityInf.readRequestClass);
    readRequest.fields = new ArrayList<>();
    DbNamedColumn.fields(readRequest.fields, this.columns, "");
    DbNamedColumn.fields(readRequest.fields, this.filters, "");

    grid = GridColumn.grid(clazz, this.columns, this.columns.sort());
    grid.setSizeFull();

    toolbar = new HorizontalLayout();
    toolbar.addClassName(CSS.TOOLBAR);
    title = VaadinUtil.label(columns.title);
    toolbar.add(title);
    filtrate = new Button(Text.Filtrate, e -> updateView());
    filtrate.setId(filtrateId(clazz));
    toolbar.add(filtrate);
    clear = new Button(Text.Clear, e -> {
      for(DbGridColumn c : columns) {
        c.component.clear();
      }
      for(DbGridColumn c : filters) {
        c.component.clear();
      }
    });
    clear.setId(clearId(clazz));
    toolbar.add(clear);
    toolbar.setAlignItems(FlexComponent.Alignment.CENTER);

    add(toolbar, grid);
    addClassName(CSS.GRID_VIEW);
    setSizeFull();
    updateView();
  }

  public void filter(Consumer<? extends FilterId> filter) {
    this.filter = filter;
    updateView();
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    title.setText(getTranslation(columns.title));
    filtrate.setText(getTranslation(Text.Filtrate));
    clear.setText(getTranslation(Text.Clear));
    GridColumn.gridLocaleChange(columns);
    for(DbGridColumn c : filters) {
      ((HasLabel)c.component).setLabel(getTranslation(c.dbName()));
    }
  }

  public final void updateView() {
    readRequest.filter.reset();
    if(filter != null) {
      filter.accept(readRequest.filter);
    }
    for(DbGridColumn c : columns) {
      c.filter.accept(readRequest.filter, c.component);
    }
    for(DbGridColumn c : filters) {
      c.filter.accept(readRequest.filter, c.component);
    }
    DbGridDataProvider<K, E> dp = new DbGridDataProvider<>(service, readRequest);
    dataView = grid.setItems(dp::get);
  }

}
