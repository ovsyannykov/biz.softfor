package biz.softfor.vaadin;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import org.apache.commons.lang3.StringUtils;

public class BasicView extends VerticalLayout
implements HasDynamicTitle, LocaleChangeObserver {

  public final String pageTitle;

  public BasicView(String pageTitle) {
    this.pageTitle = pageTitle;
  }

  public BasicView() {
    this(null);
  }

  @Override
  public String getPageTitle() {
    String result = getTranslation(MainLayout.AppTitle);
    if(StringUtils.isNotBlank(pageTitle)) {
      result = getTranslation(pageTitle) + " | " + result;
    }
    return result;
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    UI.getCurrent().getPage().setTitle(getPageTitle());
  }

}
