package biz.softfor.vaadin.field;

import biz.softfor.util.api.Identifiable;
import biz.softfor.vaadin.CSS;
import biz.softfor.vaadin.DbNamedColumn;
import biz.softfor.vaadin.Text;
import biz.softfor.vaadin.dbgrid.DbGrid;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.DialogVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import java.util.Collection;
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
  private final Button clearBtn;
  private final Button selectBtn;

  public ManyToOneBasicField(
    String name
  , DbGrid<K, E, ? extends Identifiable<K>> dbGrid
  , Function<E, String> label
  , Function<E, String> detail
  , List<String> involvedFields
  ) {
    this.dbName = name;
    this.dbGrid = dbGrid;
    this.dbGrid.grid.setId(ToManyField.gridId(this.dbName));
    this.label = label;
    this.detail = detail;
    this.involvedFields = involvedFields;
    viewCtl = new TextField();
    viewCtl.setReadOnly(true);
    viewCtl.setWidthFull();
    clearBtn = new Button(new Icon(VaadinIcon.CLOSE), e -> {
      this.dbGrid.grid.deselectAll();
      updateModel();
    });
    clearBtn.addThemeVariants(
      ButtonVariant.LUMO_SMALL
    , ButtonVariant.LUMO_TERTIARY_INLINE
    , ButtonVariant.LUMO_ERROR
    );
    clearBtn.setVisible(false);
    viewCtl.setSuffixComponent(clearBtn);
    selectBtn = new Button(new Icon(VaadinIcon.LIST_SELECT), e -> {
      this.dbGrid.updateView();
      Dialog dialog = new Dialog(this.dbGrid);
      Button select = new Button(getTranslation(Text.Select), ev -> {
        updateModel();
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
    viewCtl.setPrefixComponent(selectBtn);
    add(viewCtl);
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

  @Override
  public void setReadOnly(boolean v) {
    clearBtn.setVisible(!v);
    selectBtn.setVisible(!v);
  }

  private void updateModel() {
    V v = getModelValue();
    setPresentationValue(v);
    setModelValue(v, true);
    clearBtn.setVisible(v != null
    && !((v instanceof Collection) && ((Collection)v).isEmpty()));
  }

}
