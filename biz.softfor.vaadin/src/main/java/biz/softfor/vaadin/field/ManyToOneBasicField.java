package biz.softfor.vaadin.field;

import biz.softfor.util.Reflection;
import biz.softfor.util.api.Identifiable;
import biz.softfor.util.api.Order;
import biz.softfor.util.api.ReadRequest;
import biz.softfor.util.api.filter.FilterId;
import biz.softfor.vaadin.CSS;
import biz.softfor.vaadin.DbNamedColumn;
import biz.softfor.vaadin.Text;
import biz.softfor.vaadin.dbgrid.DbGrid;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBoxBase;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.DialogVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ManyToOneBasicField<
  K extends Number
, E extends Identifiable<K>
, F extends FilterId<K>
, V
, C extends ComboBoxBase<C, E, V>
>
extends CustomField<V> implements DbNamedColumn {

  private final String dbName;
  protected final DbGrid<K, E, ? extends Identifiable<K>, F> dbGrid;
  protected final ItemLabelGenerator<E> label;
  protected final Function<E, String> detail;
  private final List<String> involvedFields;
  private final ReadRequest<K, F> readRequest;
  protected final ComboBoxBase<C, E, V> viewCtl;
  private final Button selectBtn;

  public ManyToOneBasicField(
    String name
  , DbGrid<K, E, ? extends Identifiable<K>, F> dbGrid
  , ItemLabelGenerator<E> label
  , Function<E, String> detail
  , List<String> involvedFields
  , BiConsumer<ReadRequest<K, F>, String> fillRequest
  , C ctl
  , Function<DbGrid<K, E, ? extends Identifiable<K>, F>, V> getGridSelection
  , BiConsumer<DbGrid<K, E, ? extends Identifiable<K>, F>, V> setGridSelection
  , Function<V, String> tooltip
  ) {
    this.dbName = name;
    this.dbGrid = dbGrid;
    this.dbGrid.grid.setId(ToManyField.gridId(this.dbName));
    this.label = label;
    this.detail = detail;
    this.involvedFields = involvedFields;
    readRequest = Reflection.newInstance(this.dbGrid.entityInf.readRequestClass);
    readRequest.fields = this.involvedFields;
    readRequest.sort.clear();
    readRequest.sort
    .add(new Order(Order.Direction.ASC, readRequest.fields.getFirst()));
    viewCtl = ctl;
    viewCtl.addValueChangeListener(e -> {
      V v = e.getValue();
      viewCtl.setTooltipText(v == null ? "" : tooltip.apply(v));
    });
    viewCtl.setClearButtonVisible(true);
    viewCtl.setItemLabelGenerator(this.label);
    viewCtl.setItemsPageable((pageable, lookingFor) -> {
      if(lookingFor.isBlank()) {
        readRequest.filter.reset();
      } else {
        fillRequest.accept(readRequest, lookingFor);
      }
      readRequest.setStartRow((int) pageable.getOffset());
      readRequest.setRowsOnPage(pageable.getPageSize());
      return this.dbGrid.service.read(readRequest).getData();
    });
    viewCtl.setWidthFull();
    selectBtn = new Button(new Icon(VaadinIcon.LIST_SELECT), e -> {
      setGridSelection.accept(this.dbGrid, viewCtl.getValue());
      this.dbGrid.updateView();
      Dialog dialog = new Dialog(this.dbGrid);
      Button select = new Button(getTranslation(Text.Select), ev -> {
        setValue(getGridSelection.apply(this.dbGrid));
        dialog.close();
      });
      select.setId(ToManyField.selectId());
      Button cancel
      = new Button(getTranslation(Text.Cancel), ev -> dialog.close());
      cancel.setId(ToManyField.cancelId());
      dialog.getFooter().add(select, cancel);
      dialog.addThemeVariants(DialogVariant.LUMO_NO_PADDING);
      dialog.setMinWidth("32rem");
      dialog.setWidth(CSS.AUTO);
      dialog.setHeightFull();
      dialog.setDraggable(true);
      dialog.setResizable(true);
      dialog.open();
    });
    selectBtn.addThemeVariants
    (ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY_INLINE);
    HorizontalLayout layout = new HorizontalLayout(viewCtl, selectBtn);
    layout.setPadding(false);
    layout.setSpacing(false);
    layout.getThemeList().add("spacing-xs");
    add(layout);
  }

  @Override
  public String dbName() {
    return dbName;
  }

  @Override
  public List<String> involvedFields() {
    return involvedFields;
  }

  @Override
  protected V generateModelValue() {
    return viewCtl.getValue();
  }

  @Override
  public V getValue() {
    return viewCtl.getValue();
  }

  @Override
  public void setValue(V v) {
    super.setValue(v);
    viewCtl.setValue(v);
  }

  @Override
  protected void setPresentationValue(V v) {
    viewCtl.setValue(v);
  }

  @Override
  public void setReadOnly(boolean v) {
    viewCtl.setReadOnly(v);
    selectBtn.setVisible(!v);
  }

}
