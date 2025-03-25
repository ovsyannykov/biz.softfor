package biz.softfor.vaadin;

import biz.softfor.util.Constants;
import biz.softfor.util.Locales;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.i18n.I18NProvider;
import java.util.List;
import java.util.Locale;

public class LangSelector extends HorizontalLayout {

  private final Span label;
  private final ComboBox<Locale> ctl;

  public LangSelector(I18NProvider i18NProvider) {
    label = VaadinUtil.label("");
    List<Locale> locales = i18NProvider.getProvidedLocales();
    ctl = new ComboBox<>("", locales);
    add(label, ctl);
    setAlignItems(FlexComponent.Alignment.CENTER);
    ctl.addValueChangeListener(e -> {
      Locale newLoc = e.getValue();
      UI.getCurrent().setLocale(newLoc);
      UI.getCurrent().getSession().setLocale(newLoc);
      label.setText(getTranslation(Constants.LANGUAGE));
      ItemLabelGenerator<Locale> lg = v -> getTranslation(v.getLanguage());
      ctl.setItemLabelGenerator(lg);
      ctl.setRenderer(VaadinUtil.defaultRenderer(lg));
    });
    ctl.setValue(Locales.supported(UI.getCurrent().getLocale()));
  }

}
