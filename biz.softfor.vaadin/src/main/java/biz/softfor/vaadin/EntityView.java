package biz.softfor.vaadin;

import biz.softfor.jpa.crud.AbstractCrudSvc;
import biz.softfor.jpa.crud.querygraph.ColumnDescr;
import biz.softfor.jpa.crud.querygraph.DiffContext;
import biz.softfor.spring.security.SecurityUtil;
import biz.softfor.user.spring.SecurityMgr;
import biz.softfor.util.Reflection;
import biz.softfor.util.api.CommonResponse;
import biz.softfor.util.api.CreateRequest;
import biz.softfor.util.api.DeleteRequest;
import biz.softfor.util.api.Identifiable;
import biz.softfor.util.api.ReadRequest;
import biz.softfor.util.api.ServerError;
import biz.softfor.util.api.UpdateRequest;
import biz.softfor.util.security.UpdateClassRoleCalc;
import biz.softfor.vaadin.dbgrid.DbGrid;
import biz.softfor.vaadin.field.grid.GridField;
import biz.softfor.vaadin.field.grid.GridFields;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;

public abstract class EntityView
<K extends Number, E extends Identifiable<K>, WOR extends Identifiable<K>>
extends BasicView {

  private final SplitLayout layout;
  private final DbGrid<K, E, WOR> dbGrid;
  private final GridFields<K, E> grids;
  private final EntityForm<K, E, WOR> form;
  private final SecurityMgr securityMgr;
  private final Binder<E> binder;
  private final VerticalLayout itemsPanesContainer;
  private boolean isEdit;
  private Button edit;
  private Button add;
  private Button delete;

  public static String id(Class<?> clazz) {
    return clazz.getSimpleName() + VaadinUtil.VIEW_ID_SFX;
  }

  public static String addId() {
    return Text.Add + VaadinUtil.BUTTON_ID_OBJ + VaadinUtil.VIEW_ID_SFX;
  }

  public static String deleteId() {
    return Text.Delete + VaadinUtil.BUTTON_ID_OBJ + VaadinUtil.VIEW_ID_SFX;
  }

  public static String editId() {
    return Text.Edit + VaadinUtil.BUTTON_ID_OBJ + VaadinUtil.VIEW_ID_SFX;
  }

  public static String gridId(String name) {
    return name + VaadinUtil.GRID_ID_OBJ + VaadinUtil.VIEW_ID_SFX;
  }

  public static String gridId(Class<?> clazz) {
    return gridId(clazz.getSimpleName());
  }

  public static String viewId() {
    return Text.View + VaadinUtil.BUTTON_ID_OBJ + VaadinUtil.VIEW_ID_SFX;
  }

  protected EntityView(
    DbGrid<K, E, WOR> dbGrid
  , GridFields<K, E> grids
  , EntityForm<K, E, WOR> form
  , SecurityMgr securityMgr
  ) {
    super(dbGrid.columns.title);
    layout = new SplitLayout(SplitLayout.Orientation.HORIZONTAL);
    add(layout);
    this.dbGrid = dbGrid;
    this.grids = grids;
    this.form = form;
    this.securityMgr = securityMgr;
    if(this.dbGrid.columns.isAccessible()) {
      this.dbGrid.grid.setId(gridId(this.dbGrid.entityInf.clazz));
      for(GridField<?, ?, E, ?> c : this.grids) {
        DbNamedColumn.fields
        (this.dbGrid.readRequest.fields, c.columns, c.dbName());
      }
      if(this.grids.isEmpty()) {
        binder = null;
        itemsPanesContainer = null;
        layout.addToSecondary(this.form);
      } else {
        binder = new Binder<>(this.dbGrid.entityInf.clazz);
        itemsPanesContainer = new VerticalLayout();
        for(GridField<?, ?, E, ?> g : this.grids) {
          binder.bindReadOnly(g, g.dbName());
          this.dbGrid.grid.addCellFocusListener
          (e -> binder.setBean(e.getItem().orElse(null)));
          g.addClassNames(CSS.GRID);
          g.getContent().setMargin(false);
          g.getContent().setPadding(false);
          g.grid.setId(gridId(g.dbName()));
          itemsPanesContainer.add(g);
        }
        layout.addToSecondary(this.form, itemsPanesContainer);
      }
      isEdit = false;
      if(this.form.isAccessible()) {
        Class<?> svcClass = this.dbGrid.service.serviceClass();
        Collection<String> groups = SecurityUtil.groups();
        isEdit = !this.form.isReadOnly() && securityMgr.isMethodAllowed
        (svcClass, AbstractCrudSvc.UPDATE_METHOD, groups);
        String editLabel = isEdit ? Text.Edit : Text.View;
        edit = new Button(
          editLabel
        , e -> {
            E v = this.dbGrid.grid.asSingleSelect().getValue();
            if(v != null) {
              edit(v);
            }
          }
        );
        edit.setId(isEdit ? editId() : viewId());
        this.dbGrid.toolbar.add(edit);
        this.dbGrid.grid.addItemDoubleClickListener(e -> edit(e.getItem()));
        this.form.addSaveListener(this::save);
        if(this.form.columns.addEnabled && !this.form.isReadOnly()
        && securityMgr.isMethodAllowed
          (svcClass, AbstractCrudSvc.CREATE_METHOD, groups)) {
          add = new Button(Text.Add, e -> {
            this.dbGrid.grid.getSelectionModel().deselectAll();
            edit(Reflection.newInstance(this.dbGrid.entityInf.clazz));
          });
          add.setId(addId());
          this.dbGrid.toolbar.add(add);
        }
        if(securityMgr.isAllowed
          (new UpdateClassRoleCalc(this.dbGrid.entityInf.clazz).id(), groups)
        && securityMgr.isMethodAllowed
        (svcClass, AbstractCrudSvc.DELETE_METHOD, groups)) {
          delete = new Button(Text.Delete, this::delete);
          delete.setId(deleteId());
          delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
          this.dbGrid.toolbar.add(delete);
        }
        this.form.addCancelListener(this::cancel);
      }
      layout.addToPrimary(this.dbGrid);
      layout.setSplitterPosition(65);
      layout.setSizeFull();
      setPadding(false);
      setMargin(false);
      setSizeFull();
      setId(id(this.dbGrid.entityInf.clazz));
      edit(null);
    } else {
      binder = null;
      itemsPanesContainer = null;
      UI.getCurrent().getPage().setLocation(NotFoundView.PATH);
    }
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    super.localeChange(event);
    if(edit != null) {
      edit.setText(getTranslation(isEdit ? Text.Edit : Text.View));
    }
    if(add != null) {
      add.setText(getTranslation(Text.Add));
    }
    if(delete != null) {
      delete.setText(getTranslation(Text.Delete));
    }
  }

  private void cancel(EntityForm.CancelEvent e) {
    edit(null);
    if(!e.getSource().isAdd()) {
      E item = (E)dbGrid.grid.asSingleSelect().getValue();
      E oldItem = (E)e.oldItem;
      try {
        WOR itemWor = dbGrid.entityInf.worClass
        .getConstructor(dbGrid.entityInf.clazz).newInstance(item);
        WOR oldItemWor = dbGrid.entityInf.worClass
        .getConstructor(dbGrid.entityInf.clazz).newInstance(e.oldItem);
        DiffContext diffCtx
        = ColumnDescr.diff("", dbGrid.entityInf.worClass, itemWor, oldItemWor);
        if(diffCtx.changed) {
          dbGrid.dataView.refreshItem(oldItem);
          binder.setBean(oldItem);
        }
      }
      catch(IllegalAccessException | IllegalArgumentException
      | InstantiationException | InvocationTargetException
      | NoSuchMethodException ex) {
        throw new ServerError(ex);
      }
    }
  }

  private void delete(ClickEvent<Button> e) {
    DeleteRequest request
    = Reflection.newInstance(dbGrid.entityInf.deleteRequestClass);
    Set selected = dbGrid.grid.getSelectedItems();
    request.filter.setId(Identifiable.idList((Set<Identifiable<K>>)selected));
    securityMgr.methodCheck(
      dbGrid.service.serviceClass()
    , AbstractCrudSvc.CREATE_METHOD
    , SecurityUtil.groups()
    );
    dbGrid.service.delete(request);
    binder.removeBean();
    dbGrid.updateView();
    edit(null);
  }

  private void edit(E item) {
    if(item == null) {
      removeClassName(CSS.EDIT_FORM_EDITING);
      form.setVisible(false);
      if(itemsPanesContainer != null) {
        itemsPanesContainer.setVisible(true);
      }
    } else {
      boolean isAdd = item.getId() == null;
      if(!isAdd) {
        ReadRequest request
        = Reflection.newInstance(dbGrid.entityInf.readRequestClass);
        request.fields = form.fields;
        request.filter.assignId(item.getId());
        securityMgr.readCheck(dbGrid.service, request, SecurityUtil.groups());
        CommonResponse<E> response = dbGrid.service.read(request);
        if(CollectionUtils.isEmpty(response.getData())) {
          ConfirmDialog dialog = new ConfirmDialog();
          dialog.setHeader(getTranslation(Text.Not_found));
          dialog.setText(getTranslation(Text.The_requested_item_not_found));
          dialog.setConfirmText(getTranslation(Text.OK));
          dialog.open();
        } else {
          item = response.getData(0);
        }
      }
      form.setData(item, isAdd);
      if(itemsPanesContainer != null) {
        itemsPanesContainer.setVisible(false);
      }
      form.setVisible(true);
      addClassName(CSS.EDIT_FORM_EDITING);
    }
  }

  private void save(EntityForm.SaveEvent e) {
    E item = (E)e.item;
    try {
      WOR itemWor = dbGrid.entityInf.worClass
      .getConstructor(dbGrid.entityInf.clazz).newInstance(item);
      if(item.getId() == null) {
        securityMgr.methodCheck(
          dbGrid.service.serviceClass()
        , AbstractCrudSvc.CREATE_METHOD
        , SecurityUtil.groups()
        );
        CreateRequest<K, WOR> request = new CreateRequest<>(itemWor);
        CommonResponse<WOR> response = dbGrid.service.create(request);
        if(response.isOk()) {
          item.setId(response.getData(0).getId());
          edit(null);
          dbGrid.dataView.refreshAll();
          dbGrid.grid.select(item);
          binder.setBean(item);
        } else {
          VaadinUtil.messageDialog(getTranslation(Text.Save), response.getDescr());
        }
      } else {
        WOR oldItemWor = dbGrid.entityInf.worClass
        .getConstructor(dbGrid.entityInf.clazz).newInstance(e.oldItem);
        DiffContext diffCtx
        = ColumnDescr.diff("", dbGrid.entityInf.worClass, itemWor, oldItemWor);
        if(diffCtx.changed) {
          UpdateRequest request
          = dbGrid.entityInf.updateRequestClass.getConstructor().newInstance();
          request.data = (Identifiable<K>)diffCtx.data;
          request.fields = diffCtx.updateToNull;
          request.filter.assignId(itemWor.getId());
          securityMgr.updateCheck(
            dbGrid.entityInf.clazz
          , dbGrid.entityInf.worClass
          , request
          , SecurityUtil.groups()
          );
          CommonResponse<WOR> response = dbGrid.service.update(request);
          if(response.isOk()) {
            edit(null);
            dbGrid.dataView.refreshItem(item);
            binder.setBean(item);
          } else {
            VaadinUtil.messageDialog(getTranslation(Text.Save), response.getDescr());
          }
        }
      }
    }
    catch(IllegalAccessException | IllegalArgumentException
    | InstantiationException | InvocationTargetException
    | NoSuchMethodException ex) {
      throw new ServerError(ex);
    }
  }

}
