package biz.softfor.vaadin.field;

import biz.softfor.util.api.Identifiable;
import biz.softfor.vaadin.DbNamedColumn;
import biz.softfor.vaadin.Text;
import biz.softfor.vaadin.dbgrid.DbGrid;
import static biz.softfor.vaadin.field.ToManyField.gridId;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.DialogVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import java.util.List;
import java.util.function.Function;

public abstract class ManyToOneBasicField
<K extends Number, E extends Identifiable<K>, V> extends CustomField<V>
implements DbNamedColumn {

  private final String dbName;
  protected final DbGrid<K, E, ? extends Identifiable<K>> dbGrid;
  protected final Function<E, String> label;
  protected final Function<E, String> detail;
  private final List<String> involvedFields;
  protected final TextField viewCtl;
  protected V value;
  private final HorizontalLayout layout;
  private final Button clearBtn;
  private boolean clearBtnAdded;
  private final Button selectBtn;
  private boolean selectBtnAdded;

  public ManyToOneBasicField(
    String name
  , DbGrid<K, E, ? extends Identifiable<K>> dbGrid
  , Function<E, String> label
  , Function<E, String> detail
  , List<String> involvedFields
  ) {
    this.dbName = name;
    this.dbGrid = dbGrid;
    dbGrid.grid.setId(gridId(this.dbName));
    this.label = label;
    this.detail = detail;
    this.involvedFields = involvedFields;
    viewCtl = new TextField();
    viewCtl.setReadOnly(true);
    viewCtl.setWidthFull();
    clearBtn = new Button(new Icon(VaadinIcon.CLOSE), e -> {
      dbGrid.grid.deselectAll();
      updateModel();
    });
    clearBtn.addThemeVariants(
      ButtonVariant.LUMO_SMALL
    , ButtonVariant.LUMO_TERTIARY_INLINE
    , ButtonVariant.LUMO_ERROR
    );
    clearBtnAdded = false;
    selectBtn = new Button(new Icon(VaadinIcon.LIST_SELECT), e -> {
      Dialog dialog = new Dialog(dbGrid);

      Button select = new Button(getTranslation(Text.Select), ev -> {
        updateModel();
        dialog.close();
      });
      select.setId(ToManyField.selectId());

      Button cancel = new Button(getTranslation(Text.Cancel), ev -> dialog.close());
      cancel.setId(ToManyField.cancelId());

      dialog.getFooter().add(select, cancel);
      dialog.addThemeVariants(DialogVariant.LUMO_NO_PADDING);
      dialog.setMinWidth("32rem");
      dialog.setHeightFull();
      dialog.setDraggable(true);
      dialog.setResizable(true);
      dialog.open();
    });
    selectBtn.addThemeVariants
    (ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY_INLINE);
    selectBtnAdded = true;
    layout = new HorizontalLayout(viewCtl, selectBtn);
    layout.setWidthFull();
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
    return value;
  }

  public abstract V getModelValue();

  public void setClearButtonVisible(boolean v) {
    if(v && !clearBtnAdded) {
      layout.add(clearBtn);
    } else {
      layout.remove(clearBtn);
    }
    clearBtnAdded = v;
  }

  @Override
  public void setReadOnly(boolean v) {
    if(v && !selectBtnAdded) {
      layout.add(selectBtn);
    } else {
      layout.remove(selectBtn);
    }
    selectBtnAdded = v;
  }

  private void updateModel() {
    V v = getModelValue();
    setPresentationValue(v);
    setModelValue(v, true);
  }

}
