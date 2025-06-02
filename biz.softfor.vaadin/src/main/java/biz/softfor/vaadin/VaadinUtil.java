package biz.softfor.vaadin;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.HasAutocomplete;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.QueryParameters;
import java.util.List;
import java.util.function.Function;
import org.apache.commons.collections4.CollectionUtils;

public class VaadinUtil {

  public final static String VERSION = "24.7.3";
  public final static String BUTTON_ID_OBJ = "-BUTTON";
  public final static String COLUMN_FILTER_ID_SFX = "-COLFLTR";
  public final static String FORM_ID_SFX = "-FORM";
  public final static String FILTER_ID_SFX = "-FLTR";
  public final static String GRID_ID_OBJ = "-GRID";
  public final static String INPUT_ID_OBJ = "-INPUT";
  public final static String MAIN_ID_SFX = "-MAIN";
  public static final String RETPATH = "retpath";
  public final static String SLCT_ID_SFX = "-SLCT";
  public final static String VIEW_ID_SFX = "-VIEW";
  public final static FormLayout.ResponsiveStep[] LAYOUT_STEPS = {
    new FormLayout.ResponsiveStep("0", 1)
  , new FormLayout.ResponsiveStep("36rem", 2)
  , new FormLayout.ResponsiveStep("54rem", 3)
  , new FormLayout.ResponsiveStep("72rem", 4)
  , new FormLayout.ResponsiveStep("90rem", 5)
  , new FormLayout.ResponsiveStep("108rem", 6)
  };

  public static <E> Renderer<E> defaultRenderer(Function<E, String> label) {
    return LitRenderer.<E>of("${item.p}").withProperty("p", e -> label.apply(e));
  }

  public static Span label(String label) {
    Span result = new Span(label);
    result.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
    return result;
  }

  public static void messageDialog(String header, String message) {
    ConfirmDialog dialog = new ConfirmDialog();
    dialog.setHeader(header);
    dialog.setText(message);
    dialog.setConfirmText(dialog.getTranslation(Text.OK));
    dialog.open();
  }

  public static void navigateWithRetPath(String to) {
    UI ui = UI.getCurrent();
    if(ui != null) {
      Location active = ui.getActiveViewLocation();
      QueryParameters qps = active.getQueryParameters();
      if(CollectionUtils.isEmpty(qps.getParameters(RETPATH))) {
        qps = qps.merging(RETPATH, active.getPath());
      }
      ui.navigate(to, qps);
    }
  }

  public static void autocompleteOff(HasAutocomplete field) {
    field.getElement()
    .setAttribute(HasAutocomplete.AUTOCOMPLETE_ATTRIBUTE, "new-password");
  }

  public static void returnToRetPath() {
    UI ui = UI.getCurrent();
    if(ui != null) {
      String retPath = "";
      Location active = ui.getActiveViewLocation();
      QueryParameters qps = active.getQueryParameters();
      List<String> retPathParam = qps.getParameters(RETPATH);
      if(CollectionUtils.isNotEmpty(retPathParam)) {
        retPath = retPathParam.getFirst();
        qps = qps.excluding(RETPATH);
      }
      ui.navigate(retPath, qps);
    }
  }

}
