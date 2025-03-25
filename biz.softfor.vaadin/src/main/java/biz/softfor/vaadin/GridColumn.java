package biz.softfor.vaadin;

import biz.softfor.util.StringUtil;
import biz.softfor.util.api.Order;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrderBuilder;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import java.util.Collection;
import java.util.List;

public class GridColumn<E, C extends AbstractField>
implements DbNamedColumn/*, LocaleChangeObserver*/ {

  private final String dbName;
  public final C component;
  private final Renderer<E> renderer;
  private Grid<E> grid;

  public static String columnFilterId(String name) {
    return name + VaadinUtil.COLUMN_FILTER_ID_SFX;
  }

  public static String filterId(String name) {
    return name + VaadinUtil.COLUMN_FILTER_ID_SFX;
  }

  public GridColumn(String dbName, C component, Renderer<E> renderer) {
    this.dbName = dbName;
    this.component = component;
    this.renderer = renderer;
  }

  @Override
  public String dbName() {
    return dbName;
  }

  public static <E> Grid<E> grid(
    Class<E> clazz
  , Collection<? extends GridColumn<E, ?>> columns
  , List<Order> sort
  ) {
    Grid<E> grid = new Grid<>(clazz, false);
    if(!columns.isEmpty()) {
      grid.appendHeaderRow();
      HeaderRow headerRow = grid.appendHeaderRow();
      for(GridColumn c : columns) {
        Grid.Column<E> column = grid.addColumn(c.dbName);
        c.rendererSet(column);
        c.grid(grid);
        List<String> involvedFields = ((DbNamedColumn)c).involvedFields();
        if(!involvedFields.isEmpty()) {
          String[] sps = new String[involvedFields.size()];
          for(int i = 0; i < sps.length; ++i) {
            sps[i] = StringUtil.field(c.dbName, involvedFields.get(i));
          }
          column.setSortProperty(sps);
        }
        column.setSortable(true);
        column.setAutoWidth(true);
        headerRow.getCell(column).setComponent(c.component);
        c.component.setId(columnFilterId(c.dbName));
      }
      grid.setMultiSort(true);
      GridSortOrderBuilder<E> soBldr = new GridSortOrderBuilder<>();
      for(Order o : sort) {
        String dbName = o.getProperty();
        boolean isPresent = false;
        for(GridColumn c : columns) {
          if(dbName.equals(c.dbName)) {
            isPresent = true;
            break;
          }
        }
        if(!isPresent) {
          break;
        }
        Grid.Column<E> column = grid.getColumnByKey(o.getProperty());
        if(Order.Direction.DESC.equals(o.getDirection())) {
          soBldr.thenDesc(column);
        } else {
          soBldr.thenAsc(column);
        }
      }
      grid.sort(soBldr.build());
      grid.addClassNames(CSS.GRID);
    }
    return grid;
  }

  public static <E> void gridLocaleChange(List<? extends GridColumn> columns) {
    for(GridColumn c : columns) {
      c.localeChange(null);
    }
  }

  public void grid(Grid<E> grid) {
    this.grid = grid;
  }

  //@Override
  public void localeChange(LocaleChangeEvent event) {
    Grid.Column<E> gc = grid.getColumnByKey(dbName());
    gc.setHeader(gc.getTranslation(StringUtil.fieldToName(gc.getKey())));
    rendererSet(gc);
  }

  public final void rendererSet(Grid.Column<E> column) {
    if(renderer != null) {
      column.setRenderer(renderer);
    }
  }

  @Override
  public String toString() {
    return
    component.getClass().getName() + " " + dbName + ": " + component.getValue();
  }

}
