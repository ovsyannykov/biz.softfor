package biz.softfor.spring.jpa.crud;

import biz.softfor.address.jpa.Postcode;
import biz.softfor.partner.api.ContactDetailsFltr;
import biz.softfor.partner.api.ContactFltr;
import biz.softfor.partner.api.PartnerDetailsFltr;
import biz.softfor.partner.api.PartnerFileFltr;
import biz.softfor.partner.api.PartnerFltr;
import biz.softfor.partner.api.PersonDetailsFltr;
import biz.softfor.partner.jpa.Contact;
import biz.softfor.partner.jpa.ContactDetails;
import biz.softfor.partner.jpa.ContactDetailsWor;
import biz.softfor.partner.jpa.ContactWor;
import biz.softfor.partner.jpa.Partner;
import biz.softfor.partner.jpa.PartnerDetails;
import biz.softfor.partner.jpa.PartnerDetailsWor;
import biz.softfor.partner.jpa.PartnerFile;
import biz.softfor.partner.jpa.PartnerFileWor;
import biz.softfor.partner.jpa.PartnerWor;
import biz.softfor.partner.jpa.PersonDetails;
import biz.softfor.partner.jpa.PersonDetailsWor;
import biz.softfor.spring.jpa.crud.CrudSvc;
import biz.softfor.testutil.Check;
import biz.softfor.testutil.jpa.TestEntities;
import biz.softfor.user.api.RoleFltr;
import biz.softfor.user.api.UserFltr;
import biz.softfor.user.api.UserGroupFltr;
import biz.softfor.user.jpa.Role;
import biz.softfor.user.jpa.RoleWor;
import biz.softfor.user.jpa.User;
import biz.softfor.user.jpa.UserGroup;
import biz.softfor.user.jpa.UserGroupWor;
import biz.softfor.user.jpa.UserWor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.proxy.pojo.bytebuddy.ByteBuddyInterceptor;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@TestConfiguration
@ComponentScan
@EntityScan(basePackageClasses = { Partner.class, Postcode.class, User.class })
public class TestConfigJpaCrud {

  @TestComponent
  public static class RoleTestSvc
  extends CrudSvc<Long, Role, RoleWor, RoleFltr> {
  }

  @TestComponent
  public static class UserGroupTestSvc extends CrudSvc<Integer, UserGroup, UserGroupWor, UserGroupFltr> {
  }

  @TestComponent
  public static class UserTestSvc extends CrudSvc<Long, User, UserWor, UserFltr> {
  }

  @TestComponent
  public static class PartnerTestSvc
  extends CrudSvc<Long, Partner, PartnerWor, PartnerFltr> {
  }

  @TestComponent
  public static class ContactTestSvc
  extends CrudSvc<Long, Contact, ContactWor, ContactFltr> {
  }

  @TestComponent
  public static class ContactDetailsTestSvc
  extends CrudSvc<Long, ContactDetails, ContactDetailsWor, ContactDetailsFltr> {
  }

  @TestComponent
  public static class PersonDetailsTestSvc
  extends CrudSvc<Long, PersonDetails, PersonDetailsWor, PersonDetailsFltr> {
  }

  @TestComponent
  public static class PersonFileTestSvc
  extends CrudSvc<Long, PartnerFile, PartnerFileWor, PartnerFileFltr> {
  }

  @TestComponent
  public static class ClientDetailsTestSvc
  extends CrudSvc<Long, PartnerDetails, PartnerDetailsWor, PartnerDetailsFltr> {
  }

  @Bean
  public Check jpaCheck(ObjectMapper objectMapper) {
    return Check.builder(objectMapper)
    .ignoredTypes(ByteBuddyInterceptor.class)
    .ignoredFields(TestEntities.IGNORED_FIELDS)
    .build();
  }

}
