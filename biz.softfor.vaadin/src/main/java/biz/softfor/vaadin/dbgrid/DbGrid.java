package biz.softfor.vaadin.dbgrid;

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
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridLazyDataView;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.shared.Registration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class DbGrid
<K extends Number, E extends Identifiable<K>, WOR extends Identifiable<K>>
extends VerticalLayout implements LocaleChangeObserver {

  public final CrudSvc<K, E, WOR, ?> service;
  public final EntityInf<K, E, WOR> entityInf;
  public final DbGridColumns<K, E> columns;
  private final DbGridColumns<K, E> filters;
  public final ReadRequest readRequest;
  public final Grid<E> grid;
  public final HorizontalLayout toolbar;
  public final FormLayout filterBar;

  public GridLazyDataView<E> dataView;
  private Consumer<FilterId<K>> filter;

  private final Span title;
  private final Button filtrate;
  public final Registration clearReg;
  public final Button clearBtn;

  public static String clearId(Class<?> clazz) {
    return Text.Clear + "-" + clazz.getSimpleName() + VaadinUtil.GRID_ID_OBJ;
  }

  public static String filtrateId(Class<?> clazz) {
    return Text.Filtrate + "-" + clazz.getSimpleName() + VaadinUtil.GRID_ID_OBJ;
  }

  public DbGrid(
    CrudSvc<K, E, WOR, ?> service
  , DbGridColumns<K, E> columns
  , DbGridColumns<K, E> filters
  ) {
    this.service = service;
    Class<E> clazz = this.service.clazz();
    entityInf = ColumnDescr.getInf(clazz);
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
    title = VaadinUtil.label(this.columns.title);
    toolbar.add(title);
    filtrate = new Button(Text.Filtrate, e -> updateView());
    filtrate.setId(filtrateId(clazz));
    toolbar.add(filtrate);
    clearBtn = new Button(Text.Clear);
    clearReg = clearBtn.addClickListener(e -> {
      filterClear();
      updateView();
    });
    clearBtn.setId(clearId(clazz));
    toolbar.add(clearBtn);
    toolbar.setAlignItems(FlexComponent.Alignment.CENTER);

    filterBar = new FormLayout();
    filterBar.setResponsiveSteps(VaadinUtil.LAYOUT_STEPS);
    for(DbGridColumn c : this.filters) {
      filterBar.add(c.component);
    }

    add(toolbar, filterBar, grid);
    addClassName(CSS.GRID_VIEW);
    setSizeFull();
    updateView();
  }

  public Consumer<FilterId<K>> filter() {
    return filter;
  }

  public void filter(Consumer<FilterId<K>> filter) {
    this.filter = filter;
    updateView();
  }

  public void filterClear() {
    for(DbGridColumn c : this.columns) {
      c.component.clear();
    }
    for(DbGridColumn c : this.filters) {
      c.component.clear();
    }
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    title.setText(getTranslation(columns.title));
    filtrate.setText(getTranslation(Text.Filtrate));
    clearBtn.setText(getTranslation(Text.Clear));
    GridColumn.localeChangeColumns(columns);
    GridColumn.localeChangeFilters(filters);
  }

  public final void updateView() {
    Collection<E> v;
    if(grid.getSelectionMode() == Grid.SelectionMode.MULTI) {
      v = grid.asMultiSelect().getValue();
    } else {
      E e = grid.asSingleSelect().getValue();
      v = e == null ? List.of() : List.of(e);
    }
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
    DbGridDataProvider<K, E, ? extends FilterId<K>> dp
    = new DbGridDataProvider<>(service, readRequest);
    dataView = grid.setItems(dp::get);
    for(E ev : v) {
      grid.select(ev);
    }
  }

}
