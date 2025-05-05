package biz.softfor.vaadin;

import biz.softfor.util.StringUtil;
import biz.softfor.util.api.Order;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.HasLabel;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrderBuilder;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import java.util.Collection;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class GridColumn<E, C extends AbstractField> implements DbNamedColumn {

  private final String dbName;
  public final C component;
  private Renderer<E> renderer;
  private Grid<E> grid;
  private final String label;

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
    String l = ((HasLabel)this.component).getLabel();
    if(StringUtils.isBlank(l)) {
      if(this.component instanceof DbNamedColumn dbNamedColumn) {
        l = dbNamedColumn.dbName();
      } else {
        l = StringUtil.fieldToName(this.dbName);
      }
    }
    label = l;
  }

  @Override
  public String dbName() {
    return dbName;
  }

  public void setRenderer(Renderer<E> renderer) {
    this.renderer = renderer;
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

  public static <E> void localeChangeColumns(List<? extends GridColumn> columns) {
    for(GridColumn c : columns) {
      c.localeChangeColumn(null);
    }
  }

  public static <E> void localeChangeFilters(List<? extends GridColumn> filters) {
    for(GridColumn c : filters) {
      c.localeChangeFilter(null);
    }
  }

  public void grid(Grid<E> grid) {
    this.grid = grid;
  }

  public void localeChangeColumn(LocaleChangeEvent event) {
    Grid.Column<E> gc = grid.getColumnByKey(dbName());
    gc.setHeader(gc.getTranslation(label));
    rendererSet(gc);
  }

  public void localeChangeFilter(LocaleChangeEvent event) {
    ((HasLabel)component).setLabel(component.getTranslation(label));
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
