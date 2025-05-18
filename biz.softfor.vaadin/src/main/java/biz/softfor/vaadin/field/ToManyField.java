package biz.softfor.vaadin.field;

import biz.softfor.jpa.crud.querygraph.ColumnDescr;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.util.api.Identifiable;
import biz.softfor.util.api.filter.Expr;
import biz.softfor.util.api.filter.FilterId;
import biz.softfor.vaadin.ColumnSecured;
import biz.softfor.vaadin.Text;
import biz.softfor.vaadin.VaadinUtil;
import biz.softfor.vaadin.dbgrid.DbGrid;
import biz.softfor.vaadin.field.grid.GridField;
import biz.softfor.vaadin.field.grid.GridFieldColumns;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.DialogVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.apache.commons.collections4.CollectionUtils;

public class ToManyField
<K extends Number, E extends Identifiable<K>, M, V extends Collection<E>>
extends GridField<K, E, M, V> {

  private Button add;
  private Button delete;

  public static String addId(String name) {
    return name + "-" + Text.Add + VaadinUtil.BUTTON_ID_OBJ;
  }

  public static String cancelId() {
    return Text.Cancel + VaadinUtil.SLCT_ID_SFX;
  }

  public static String deleteId(String name) {
    return name + "-" + Text.Delete + VaadinUtil.BUTTON_ID_OBJ;
  }

  public static String gridId(String name) {
    return name + VaadinUtil.SLCT_ID_SFX;
  }

  public static String selectId() {
    return Text.Select + VaadinUtil.SLCT_ID_SFX;
  }

  public ToManyField(
    String dbName
  , Class<M> parent
  , Supplier<V> defaultValue
  , GridFieldColumns<K, E> columns
  , DbGrid<K, E, ? extends Identifiable<K>> dbGrid
  , SecurityMgr securityMgr
  , boolean isManyToMany
  ) {
    super(dbName, dbGrid.service.clazz(), defaultValue, columns);
    Map<String, ColumnDescr> cds = ColumnDescr.get(parent);
    ColumnSecured cs
    = new ColumnSecured(securityMgr, cds, dbName, Collections.EMPTY_LIST);
    if(!cs.isReadOnly()) {
      if(isManyToMany) {
        dbGrid.grid.setId(gridId(dbName()));
        dbGrid.grid.setSelectionMode(Grid.SelectionMode.MULTI);
        add = new Button(Text.Add, e -> {
          V current = getValue();
          Consumer<FilterId<K>> filter
          = CollectionUtils.isEmpty(current) ? null : f -> f.andAnd
          (new Expr(Expr.NOT_IN, Identifiable.ID, Identifiable.idSet(current)));
          dbGrid.filter(filter);
          Dialog dialog = new Dialog(dbGrid);

          Button select = new Button(getTranslation(Text.Select), ev -> {
            dialog.close();
            V newValue = emptyValue.get();
            V startValue = getValue();
            if(CollectionUtils.isNotEmpty(startValue)) {
              newValue.addAll(startValue);
            }
            newValue.addAll(dbGrid.grid.getSelectedItems());
            CollectionUtils.addAll(current, startValue);
            setValue(newValue);
          });
          select.setId(selectId());

          Button cancel
          = new Button(getTranslation(Text.Cancel), ev -> dialog.close());
          cancel.setId(cancelId());

          dialog.getFooter().add(select, cancel);
          dialog.addThemeVariants(DialogVariant.LUMO_NO_PADDING);
          dialog.setWidthFull();
          dialog.setHeightFull();
          dialog.setDraggable(true);
          dialog.setResizable(true);
          dialog.open();
        });
        add.setId(addId(dbName()));
        add.addThemeVariants(ButtonVariant.LUMO_SMALL);
        toolbar.add(add);
      }
      delete = new Button(Text.Delete, e -> {
        Set<E> items = grid.getSelectionModel().getSelectedItems();
        grid.getListDataView().removeItems(items);
        grid.getSelectionModel().deselectAll();
      });
      delete.setId(deleteId(dbName()));
      delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
      toolbar.add(delete);
    }
  }

  public ToManyField(
    String dbName
  , Class<M> parent
  , Supplier<V> defaultValue
  , GridFieldColumns<K, E> columns
  , DbGrid<K, E, ? extends Identifiable<K>> dbGrid
  , SecurityMgr securityMgr
  ) {
    this(
      dbName
    , parent
    , defaultValue
    , columns
    , dbGrid
    , securityMgr
    , true
    );
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    super.localeChange(event);
    if(add != null) {
      add.setText(getTranslation(Text.Add));
    }
    if(delete != null) {
      delete.setText(getTranslation(Text.Delete));
    }
  }

}
