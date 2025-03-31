package biz.softfor.vaadin.demo;

import biz.softfor.util.BooleansEnum;
import biz.softfor.vaadin.CSS;
import biz.softfor.vaadin.MainLayout;
import biz.softfor.vaadin.dbgrid.BasicComboBoxDbGridColumn;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Route(value = "SampleTextFieldLabel", layout = MainLayout.class)
@AnonymousAllowed
@CssImport(value = "./textfieldlabelpos.css", themeFor = CSS.VAADIN_TEXTFIELD)
public class SampleTextFieldLabel extends VerticalLayout {

  public SampleTextFieldLabel() {
    ComboBox<BooleansEnum> lcombo = new ComboBox<>("Label left", BooleansEnum.VALUES);
    BasicComboBoxDbGridColumn.localeChange(lcombo);
    lcombo.addThemeName(CSS.LABEL_LEFT);
    TextField textFieldLeft = new TextField("Label left");
    textFieldLeft.addThemeName(CSS.LABEL_LEFT);
    ComboBox<BooleansEnum> rcombo = new ComboBox<>("Label right", BooleansEnum.VALUES);
    BasicComboBoxDbGridColumn.localeChange(rcombo);
    rcombo.addThemeName(CSS.LABEL_RIGHT);
    TextField textFieldRight = new TextField("Label right");
    textFieldRight.addThemeName(CSS.LABEL_RIGHT);
    TextField textField = new TextField("Label top");
    ComboBox<BooleansEnum> combo = new ComboBox<>("Combo", BooleansEnum.VALUES);
    BasicComboBoxDbGridColumn.localeChange(combo);
    add(lcombo, textFieldLeft, rcombo, textFieldRight, combo, textField);
  }

}
