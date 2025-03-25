package biz.softfor.vaadin.demo;

import biz.softfor.partner.jpa.Partner_;
import biz.softfor.vaadin.MenuItemData;
import biz.softfor.vaadin.Text;
import biz.softfor.vaadin.VaadinUtil;
import biz.softfor.vaadin.address.CitiesView;
import biz.softfor.vaadin.address.CityDbGridColumns;
import biz.softfor.vaadin.address.CityTypeDbGridColumns;
import biz.softfor.vaadin.address.CityTypesView;
import biz.softfor.vaadin.address.CountriesView;
import biz.softfor.vaadin.address.CountryDbGridColumns;
import biz.softfor.vaadin.address.DistrictDbGridColumns;
import biz.softfor.vaadin.address.DistrictsView;
import biz.softfor.vaadin.address.PostcodeDbGridColumns;
import biz.softfor.vaadin.address.PostcodesView;
import biz.softfor.vaadin.address.StateDbGridColumns;
import biz.softfor.vaadin.address.StatesView;
import biz.softfor.vaadin.partner.AppointmentDbGridColumns;
import biz.softfor.vaadin.partner.AppointmentsView;
import biz.softfor.vaadin.partner.ContactDbGridColumns;
import biz.softfor.vaadin.partner.ContactTypeDbGridColumns;
import biz.softfor.vaadin.partner.ContactTypesView;
import biz.softfor.vaadin.partner.ContactsView;
import biz.softfor.vaadin.partner.LocationTypeDbGridColumns;
import biz.softfor.vaadin.partner.LocationTypesView;
import biz.softfor.vaadin.partner.PartnerDbGridColumns;
import biz.softfor.vaadin.partner.PartnerFileDbGridColumns;
import biz.softfor.vaadin.partner.PartnerFilesView;
import biz.softfor.vaadin.partner.PartnersView;
import biz.softfor.vaadin.user.RoleDbGridColumns;
import biz.softfor.vaadin.user.RolesView;
import biz.softfor.vaadin.user.UserDbGridColumns;
import biz.softfor.vaadin.user.UserGroupDbGridColumns;
import biz.softfor.vaadin.user.UserGroupsView;
import biz.softfor.vaadin.user.UsersView;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.spring.annotation.EnableVaadin;
import com.vaadin.flow.theme.Theme;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;

@SpringBootApplication
@ComponentScan(basePackageClasses = { VaadinUtil.class })
@EnableVaadin({ "biz.softfor.vaadin" })
@Theme(value = "biz.softfor.vaadin.demo")
@PWA(name = "Vaadin Demo", shortName = "Vaadin Demo", offlinePath = "offline.html", offlineResources = { "images/offline.png" })
public class App implements AppShellConfigurator {

  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }

  @Bean
  @Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  public MenuItemData[] menuItemData(
    UserDbGridColumns userColumns
  , UserGroupDbGridColumns userGroupColumns
  , RoleDbGridColumns roleColumns
  , PostcodeDbGridColumns postcodeColumns
  , DistrictDbGridColumns districtColumns
  , StateDbGridColumns stateColumns
  , CountryDbGridColumns countryColumns
  , CityDbGridColumns cityColumns
  , CityTypeDbGridColumns cityTypeColumns
  , PartnerDbGridColumns partnerColumns
  , ContactDbGridColumns contactColumns
  , ContactTypeDbGridColumns contactTypeColumns
  , AppointmentDbGridColumns appointmentColumns
  , LocationTypeDbGridColumns locationTypeColumns
  , PartnerFileDbGridColumns partnerFileColumns
  ) {
    return new MenuItemData[] {
      new MenuItemData(
        partnerColumns.title
      , new MenuItemData(PartnersView.class, partnerColumns)
      , new MenuItemData(PartnerFilesView.class, partnerFileColumns)
      , new MenuItemData(ContactsView.class, contactColumns)
      , new MenuItemData(ContactTypesView.class, contactTypeColumns)
      , new MenuItemData(AppointmentsView.class, appointmentColumns)
      , new MenuItemData(LocationTypesView.class, locationTypeColumns)
      )
    , new MenuItemData(
        Partner_.ADDRESS
      , new MenuItemData(PostcodesView.class, postcodeColumns)
      , new MenuItemData(DistrictsView.class, districtColumns)
      , new MenuItemData(StatesView.class, stateColumns)
      , new MenuItemData(CountriesView.class, countryColumns)
      , new MenuItemData(CitiesView.class, cityColumns)
      , new MenuItemData(CityTypesView.class, cityTypeColumns)
      )
    , new MenuItemData(
        Text.Administration
      , new MenuItemData(UsersView.class, userColumns)
      , new MenuItemData(RolesView.class, roleColumns)
      , new MenuItemData(UserGroupsView.class, userGroupColumns)
      )
    , new MenuItemData(
        "Debug"
      , new MenuItemData("Debug", SampleTextFieldLabel.class)
      )
    };
  }

}
