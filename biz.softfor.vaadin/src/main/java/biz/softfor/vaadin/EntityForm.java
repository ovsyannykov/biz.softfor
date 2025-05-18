package biz.softfor.vaadin;

import biz.softfor.jpa.crud.querygraph.ColumnDescr;
import biz.softfor.util.StringUtil;
import biz.softfor.util.api.Identifiable;
import biz.softfor.util.api.ServerError;
import biz.softfor.vaadin.dbgrid.BasicComboBoxDbGridColumn;
import biz.softfor.vaadin.field.grid.GridField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasLabel;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBoxBase;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.shared.Registration;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.java.Log;

@Log
public abstract class EntityForm
<K extends Number, E extends Identifiable<K>, WOR extends Identifiable<K>>
extends BasicView implements Secured {

  protected final String title;
  public final EntityFormColumns columns;
  protected final Validator validator;
  public final List<String> fields;
  private final Span titleLbl;
  protected final FormLayout propertiesPane;
  protected final Binder<E> binder;
  private final Button save;
  private final Button cancel;
  private E oldData;
  private boolean isAdd;

  public static String cancelId() {
    return Text.Cancel + VaadinUtil.FORM_ID_SFX;
  }

  public static String fieldId(String name) {
    return name + VaadinUtil.INPUT_ID_OBJ + VaadinUtil.FORM_ID_SFX;
  }

  public static String fieldGridId(String name) {
    return name + VaadinUtil.FORM_ID_SFX;
  }

  public static String id(Class<?> clazz) {
    return clazz.getSimpleName() + VaadinUtil.FORM_ID_SFX;
  }

  public static String saveId() {
    return Text.Save + VaadinUtil.FORM_ID_SFX;
  }

  protected EntityForm(String title, EntityFormColumns columns, Validator validator) {
    super(title);
    this.title = title;
    this.columns = columns;
    this.validator = validator;
    if(isAccessible()) {
      fields = new ArrayList<>();
      titleLbl = VaadinUtil.label(this.title);
      binder = new BeanValidationBinder<>(this.columns.entityInf.clazz);
      HorizontalLayout toolbar = new HorizontalLayout(titleLbl);
      toolbar.setAlignItems(FlexComponent.Alignment.CENTER);
      propertiesPane = new FormLayout(toolbar);
      propertiesPane.addClassName(CSS.EDIT_FORM);
      propertiesPane.setResponsiveSteps(VaadinUtil.LAYOUT_STEPS);
      VerticalLayout formPane = new VerticalLayout(toolbar, propertiesPane);
      List<GridField<?, ?, E, ?>> grids = new ArrayList<>();
      for(Map.Entry<String, ?> p
      : (Set<Map.Entry<String, ?>>)this.columns.entrySet()) {
        String fieldName = p.getKey();
        HasValue fieldCtl = (HasValue)p.getValue();
        binder.bind(fieldCtl, fieldName);
        if(fieldCtl instanceof GridField gridField) {
          GridField<?, ?, E, ?> g = gridField;
          DbNamedColumn.fields(fields, g.columns, fieldName);
          g.grid.setId(fieldGridId(g.dbName()));
          g.setId(fieldId(g.dbName()));
          grids.add(g);
        } else {
          fields.add(fieldName);
          propertiesPane.add((Component)fieldCtl);
          ((HasSize)fieldCtl).setWidthFull();
        }
      }
      if(grids.isEmpty()) {
        add(formPane);
      } else {
        SplitLayout splitter = new SplitLayout(SplitLayout.Orientation.HORIZONTAL);
        splitter.addToPrimary(formPane);
        splitter.addToSecondary(grids.toArray(Component[]::new));
        splitter.setSplitterPosition(65);
        splitter.setWidthFull();
        add(splitter);
      }
      if(isReadOnly()) {
        save = null;
      } else {
        save = new Button(Text.Save, e -> {
          try {
            E data = (E)this.columns.entityInf.clazz.getConstructor().newInstance();
            data.setId(oldData.getId());
            ColumnDescr.initOneToOnes(data, this.columns.entityInf.clazz);
            binder.writeBeanAsDraft(data, true);
            onSave(data);
            fireEvent(new SaveEvent(this, oldData, data));
          }
          catch(IllegalAccessException | IllegalArgumentException
          | InstantiationException | InvocationTargetException | NoSuchMethodException
          | SecurityException ex) {
            throw new ServerError(ex);
          }
        });
        save.addThemeVariants(ButtonVariant.LUMO_SMALL);
        save.addClickShortcut(Key.ENTER);
        save.setId(saveId());
        toolbar.add(save);
        binder.addStatusChangeListener(e -> {
          try {
            E data = (E)this.columns.entityInf.clazz.getConstructor().newInstance();
            ColumnDescr.initOneToOnes(data, this.columns.entityInf.clazz);
            binder.writeBeanAsDraft(data, true);
            save.setEnabled(isValid(data));
          }
          catch(IllegalAccessException | IllegalArgumentException
          | InstantiationException | InvocationTargetException
          | NoSuchMethodException | SecurityException ex) {
            throw new ServerError(ex);
          }
        });
      }
      cancel
      = new Button(Text.Cancel, e -> fireEvent(new CancelEvent(this, oldData)));
      cancel.addClickShortcut(Key.ESCAPE);
      cancel.addThemeVariants
      (ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
      cancel.setId(cancelId());
      toolbar.add(cancel);
      setWidthFull();
      setId(id(this.columns.entityInf.clazz));
    } else {
      fields = null;
      titleLbl = null;
      binder = null;
      propertiesPane = null;
      save = null;
      cancel = null;
    }
  }

  public boolean isAdd() {
    return isAdd;
  }

  @Override
  public boolean isAccessible() {
    return columns.isAccessible();
  }

  @Override
  public boolean isReadOnly() {
    return columns.isReadOnly();
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    super.localeChange(event);
    titleLbl.setText(getTranslation(title));
    if(save != null) {
      save.setText(getTranslation(Text.Save));
    }
    if(cancel != null) {
      cancel.setText(getTranslation(Text.Cancel));
    }
    for(Map.Entry<String, ?> me : (Set<Map.Entry<String, ?>>)columns.entrySet()) {
      Object c = me.getValue();
      if(c instanceof HasLabel hl) {
        hl.setLabel(getTranslation(StringUtil.fieldToName(me.getKey())));
      }
      switch(c) {
        case ComboBoxBase cb -> BasicComboBoxDbGridColumn.localeChange(cb);
        case DatePicker dp -> DatePickerI18n.localeChange(dp, event);
        default -> {}
      }
    }
  }

  public void setData(E data, boolean isAdd) {
    this.isAdd = isAdd;
    try {
      ColumnDescr.initOneToOnes(data, columns.entityInf.clazz);
      oldData = (E)ColumnDescr.copyByFields(data, columns.entityInf.clazz, fields);
    }
    catch(IllegalAccessException | InstantiationException
    | InvocationTargetException | NoSuchMethodException ex) {
      throw new ServerError(ex);
    }
    binder.setBean(data);
  }

  public static abstract class FormEvent extends ComponentEvent<EntityForm> {

    public final Object oldItem;

    protected FormEvent(EntityForm source, Object oldItem) {
      super(source, false);
      this.oldItem = oldItem;
    }

  }

  public static class CancelEvent extends FormEvent {

    public CancelEvent(EntityForm source, Object oldItem) {
      super(source, oldItem);
    }

  }

  public static class SaveEvent extends FormEvent {

    public final Object item;

    public SaveEvent(EntityForm source, Object oldItem, Object item) {
      super(source, oldItem);
      this.item = item;
    }

  }

  public Registration addCancelListener
  (ComponentEventListener<CancelEvent> listener) {
    return addListener(CancelEvent.class, listener);
  }

  public Registration addSaveListener
  (ComponentEventListener<SaveEvent> listener) {
    return addListener(SaveEvent.class, listener);
  }

  protected boolean isValid(E data) {
    boolean result;
    if(isAdd) {
      result = binder.isValid();
    } else {
      try {
        Set<ConstraintViolation<?>> validate = new HashSet<>();
        WOR dataWor = (WOR)columns.entityInf.worClass
        .getConstructor(columns.entityInf.clazz).newInstance(data);
        ColumnDescr.validate
        (validator, validate, columns.entityInf.worClass, dataWor, fields);
        result = validate.isEmpty();
      } catch(NoSuchMethodException | SecurityException | InstantiationException
      | IllegalAccessException | IllegalArgumentException
      | InvocationTargetException ex) {
        throw new ServerError(ex);
      }
    }
    return result;
  }

  protected E onSave(E data) {
    return data;
  }

}
