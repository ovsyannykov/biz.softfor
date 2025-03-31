package biz.softfor.vaadin.dbgrid;

import biz.softfor.util.Labeled;
import biz.softfor.util.api.filter.FilterId;
import biz.softfor.vaadin.CSS;
import biz.softfor.vaadin.VaadinUtil;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBoxBase;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBoxVariant;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class BasicComboBoxDbGridColumn<E, V, VL extends Labeled, F extends FilterId>
extends DbGridColumn<E, V, MultiSelectComboBox<VL>, Set<VL>, F> {

  public static <VL extends Labeled> void configure
  (MultiSelectComboBox<VL> component, VL[] items) {
    component.setItems(items);
    component.setClearButtonVisible(true);
    component.addThemeVariants(MultiSelectComboBoxVariant.LUMO_SMALL);
    component.setWidthFull();
    component.setMaxWidth(CSS.PC100);
  }

  public static <E, VL extends Labeled>
  Renderer<E> defaultRenderer(Function<E, VL> getter) {
    return VaadinUtil.defaultRenderer
    (e -> UI.getCurrent().getTranslation(getter.apply(e).label()));
  }

  public static <VL extends Labeled> void localeChange(ComboBoxBase c) {
    ItemLabelGenerator<VL> lg = v -> c.getTranslation(v.label());
    c.setItemLabelGenerator(lg);
    c.setRenderer(VaadinUtil.defaultRenderer(lg));
  }

  public BasicComboBoxDbGridColumn(
    String dbName
  , VL[] items
  , Renderer<E> renderer
  , BiConsumer<F, MultiSelectComboBox<VL>> filter
  ) {
    super(dbName, new MultiSelectComboBox<>(), renderer, filter);
    configure(component, items);
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    super.localeChange(event);
    localeChange(component);
  }

}
