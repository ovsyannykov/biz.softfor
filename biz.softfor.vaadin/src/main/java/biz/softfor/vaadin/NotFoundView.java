package biz.softfor.vaadin;

import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route(value = NotFoundView.PATH, layout = MainLayout.class)
@AnonymousAllowed
public class NotFoundView extends BasicView {

  public final static String PATH = "not_found";

  private final H3 message;

  public NotFoundView() {
    super(Text.Page_not_found);
    message = new H3(getTranslation(Text.Page_not_found));
    add(message);
    setJustifyContentMode(JustifyContentMode.CENTER);
    setAlignItems(Alignment.CENTER);
    setSizeFull();
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    super.localeChange(event);
    message.setText(getTranslation(Text.Page_not_found));
  }

}
