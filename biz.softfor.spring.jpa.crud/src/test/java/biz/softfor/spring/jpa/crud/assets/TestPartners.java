package biz.softfor.spring.jpa.crud.assets;

import biz.softfor.partner.jpa.Contact;
import biz.softfor.partner.jpa.ContactDetails;
import biz.softfor.partner.jpa.ContactType;
import biz.softfor.partner.jpa.ContactWor;
import biz.softfor.partner.jpa.Contact_;
import biz.softfor.partner.jpa.LocationType;
import biz.softfor.partner.jpa.Partner;
import biz.softfor.partner.jpa.PartnerDetails;
import biz.softfor.partner.jpa.PartnerDetailsWor;
import biz.softfor.partner.jpa.PartnerFile;
import biz.softfor.partner.jpa.PartnerWor;
import biz.softfor.partner.jpa.Partner_;
import biz.softfor.partner.jpa.PersonDetails;
import biz.softfor.partner.jpa.PersonDetailsWor;
import biz.softfor.testutil.Check;
import biz.softfor.testutil.IgnoringFields;
import biz.softfor.testutil.TestData;
import biz.softfor.testutil.jpa.SelectQuery;
import biz.softfor.testutil.jpa.TestEntities;
import biz.softfor.user.jpa.Role;
import biz.softfor.user.jpa.Role_;
import biz.softfor.user.jpa.User;
import biz.softfor.user.jpa.UserGroup;
import biz.softfor.user.jpa.UserGroup_;
import biz.softfor.user.jpa.UserWor;
import biz.softfor.user.jpa.User_;
import biz.softfor.util.Json;
import static biz.softfor.util.StringUtil.field;
import biz.softfor.util.partner.PartnerType;
import biz.softfor.util.security.DefaultAccess;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import lombok.extern.java.Log;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import static org.assertj.core.util.Sets.set;
import org.springframework.transaction.PlatformTransactionManager;

@Log
public class TestPartners extends TestData {

  public final TestEntities[] testEntities;

  public final TestEntities<Long, Partner> partners;
  public final TestEntities<Long, PartnerDetails> partnerDetails;
  public final TestEntities<Long, PersonDetails> personDetails;
  public final TestEntities<Long, PartnerFile> partnerFiles;
  public final TestEntities<Long, Contact> contacts;
  public final TestEntities<Long, ContactDetails> contactDetails;
  public final TestEntities<Short, ContactType> contactTypes;
  public final TestEntities<Short, LocationType> locationTypes;
  public final TestEntities<Long, User> users;
  public final TestEntities<Integer, UserGroup> groups;
  public final TestEntities<Long, Role> roles;

  public final IgnoringFields partnerIgnoringFields;
  public final IgnoringFields contactIgnoringFields;
  public final IgnoringFields userIgnoringFields;
  public final IgnoringFields rolesIgnoringFields;
  public final FilterProvider jsonFilter;

  public final static BiFunction<String, Object, PartnerWor>
  newPartnerWor = (l, i) -> {
    String label = l + i;
    PartnerWor result = new PartnerWor();
    result.setPartnerName(label);
    result.setPartnerRegdate(LocalDate.of(2022, 1, 20 + (int)i));
    result.setPartnerRegcode("Regcode" + l.substring(0, Math.min(15, l.length())) + i);
    result.setAddress("Address" + label);
    result.setPartnerFullname("Fullname" + label);
    result.setTyp(PartnerType.EMPLOYEE);
    return result;
  };

  public final static BiFunction<String, Object, Partner>
  newPartner = (l, i) -> {
    String label = l + i;
    Partner result = new Partner();
    result.setPartnerName(label);
    result.setPartnerRegdate(LocalDate.of(2022, 1, 20 + (int)i));
    result.setPartnerRegcode("Regcode" + l.substring(0, Math.min(15, l.length())) + i);
    result.setAddress("Address" + label);
    result.setPartnerFullname("Fullname" + label);
    result.setTyp(PartnerType.EMPLOYEE);
    return result;
  };

  /*public final static BiFunction<String, Object, AppointmentWor>
  newAppointmentWor = (l, i) -> {
    String label = l + i;
    AppointmentWor result = new AppointmentWor();
    result.setName(label);
    result.setDescr(label + " description");
    return result;
  };

  public final static BiFunction<String, Object, Appointment>
  newAppointment = (l, i) -> {
    String label = l + i;
    Appointment result = new Appointment();
    result.setName(label);
    result.setDescr(label + " description");
    return result;
  };*/

  public final static BiFunction<String, Object, ContactWor>
  newContactWor = (l, i) -> {
    String label = l + i;
    ContactWor result = new ContactWor();
    result.setDescr(label);
    result.setIsPublic(true);
    return result;
  };

  public final static BiFunction<String, Object, Contact>
  newContact = (l, i) -> {
    String label = l + i;
    Contact result = new Contact();
    result.setDescr(label);
    result.setIsPublic(true);
    return result;
  };

  public final static BiFunction<String, Object, ContactDetails>
  newContactDetails = (l, i) -> {
    String label = l + i;
    ContactDetails result = new ContactDetails();
    result.setNote("Note" + label);
    return result;
  };

  public final static BiFunction<String, Object, ContactType>
  newContactType = (l, i) -> {
    String label = l + i;
    ContactType result = new ContactType();
    result.setName("ContactType" + label);
    result.setDescr("ContactTypeDescr" + label);
    return result;
  };

  public final static BiFunction<String, Object, LocationType>
  newLocationType = (l, i) -> {
    String label = l + i;
    LocationType result = new LocationType();
    result.setName("LocationType" + label);
    result.setDescr("LocationTypeDescr" + label);
    return result;
  };

  public final static BiFunction<String, Object, PartnerDetails>
  newPartnerDetails = (l, i) -> {
    String label = l + i;
    PartnerDetails result = new PartnerDetails();
    result.setNote("Note" + label);
    return result;
  };

  public final static BiFunction<String, Object, PartnerDetailsWor>
  newPartnerDetailsWor = (l, i) -> {
    String label = l + i;
    PartnerDetailsWor result = new PartnerDetailsWor();
    result.setNote("Note" + label);
    return result;
  };

  public final static BiFunction<String, Object, PersonDetailsWor>
  newPersonDetailsWor = (l, i) -> {
    String label = l + i;
    PersonDetailsWor result = new PersonDetailsWor();
    result.setPassportSeries("S" + StringUtils.right(String.valueOf(i), 6));
    result.setPassportNumber(((Number)i).intValue());
    result.setPassportDate(LocalDate.of(2022, 1, 24));
    result.setMiddlename("Middlename" + label);
    result.setPassportIssued("PassportIssued" + label);
    result.setMarried(Boolean.FALSE);
    return result;
  };

  public final static BiFunction<String, Object, PersonDetails>
  newPersonDetails = (l, i) -> {
    String label = l + i;
    PersonDetails result = new PersonDetails();
    result.setPassportSeries("S" + StringUtils.right(String.valueOf(i), 6));
    result.setPassportNumber(((Number)i).intValue());
    result.setPassportDate(LocalDate.of(2022, 1, 24));
    result.setMiddlename("Middlename" + label);
    result.setPassportIssued("PassportIssued" + label);
    result.setMarried(Boolean.FALSE);
    return result;
  };

  public final static BiFunction<String, Object, PartnerFile>
  newPartnerFile = (l, i) -> {
    String label = l + i;
    PartnerFile result = new PartnerFile();
    result.setDescr("Descr" + label);
    result.setUri("http://localhost/" + label);
    return result;
  };

  public final static BiFunction<String, Object, UserWor>
  newUserWor = (l, i) -> {
    String label = l + i.toString();
    UserWor result = new UserWor();
    result.setUsername("Username" + label);
    result.setPassword("Password" + label);
    result.setEmail(label + "@t.co");
    return result;
  };

  public final static BiFunction<String, Object, User> newUser = (l, i) -> {
    String label = l + i.toString();
    User result = new User();
    result.setUsername("Username" + label);
    result.setPassword("Password" + label);
    result.setEmail(label + "@t.co");
    return result;
  };

  public final static BiFunction<String, Object, Role>
  newRole = (l, i) -> {
    String label = l + i.toString();
    Role result = new Role();
    result.setId(((Number)i).longValue());
    result.setDefaultAccess(DefaultAccess.EVERYBODY);
    result.setIsUrl(Boolean.FALSE);
    result.setUpdateFor(Boolean.FALSE);
    result.setDisabled(Boolean.FALSE);
    result.setOrphan(Boolean.FALSE);
    result.setDeniedForAll(Boolean.FALSE);
    result.setName(Role_.NAME + label);
    result.setObjName(Role_.OBJ_NAME + label);
    result.setDescription(Role_.DESCRIPTION + label);
    return result;
  };

  public final static BiFunction<String, Object, UserGroup> newGroup = (l, i) -> {
    String label = l + i.toString();
    UserGroup result = new UserGroup();
    result.setName(label);
    return result;
  };

  public final static String[] ACCESS_ACTION_FETCH_RELATIONS = { Role_.GROUPS };

  public final static String[] PARTNER_FETCH_RELATIONS = {
    Partner_.USERS
  , Partner_.CONTACTS
  , field(Partner_.CONTACTS, Contact_.CONTACT_DETAILS)
  , Partner_.LOCATION_TYPE
  , Partner_.PARENT
  , Partner_.PARTNER_DETAILS
  , Partner_.PERSON_DETAILS
  , Partner_.PARTNER_FILES
  };
  public final static Set<String> partnerIgnoringFieldsSet
  = set(Partner_.POSTCODE);
  public final static Set<String> contactIgnoringFieldsSet
  = set(Contact_.PARTNER, Contact_.APPOINTMENT);

  public final static String[] USER_FETCH_RELATIONS = { User_.GROUPS };

  private final EntityManager em;

  public TestPartners(
    String label
  , int size
  , Check check
  , ObjectMapper om
  , EntityManager em
  , PlatformTransactionManager tm
  ) {
    super(om);
    this.em = em;
    contactIgnoringFields = new IgnoringFields(Contact.class
    , contactIgnoringFieldsSet, Partner_.CONTACTS);
    partnerIgnoringFields = new IgnoringFields(Partner.class
    , partnerIgnoringFieldsSet, "", Partner_.PARENT);
    partnerIgnoringFields.children.add(contactIgnoringFields);
    partnerIgnoringFields.children.add(new IgnoringFields(User.class
    , set(User_.GROUPS), Partner_.USERS));
    jsonFilter = partnerIgnoringFields.jsonFilter();
    contactTypes = new TestEntities<>("contactTypes", label, size, ContactType.class
    , newContactType, new SelectQuery<>(em, ContactType.class)
    , check, om, em, tm);
    locationTypes = new TestEntities<>("locationTypes", label, size, LocationType.class
    , newLocationType, new SelectQuery<>(em, LocationType.class)
    , check, om, em, tm);
    userIgnoringFields = new IgnoringFields(User.class);
    userIgnoringFields.children.add(new IgnoringFields
    (UserGroup.class, set(UserGroup_.ROLES, UserGroup_.USERS), field(User_.GROUPS)));
    groups = new TestEntities<>("groups", label, size, UserGroup.class, newGroup
    , new SelectQuery<>(em, UserGroup.class, UserGroup_.ROLES, UserGroup_.USERS)
    , check, om, em, tm);
    rolesIgnoringFields = new IgnoringFields(Role.class);
    rolesIgnoringFields.children.add(new IgnoringFields
    (UserGroup.class, set(UserGroup_.USERS, UserGroup_.ROLES), Role_.GROUPS));
    rolesIgnoringFields.children.add(new IgnoringFields
    (User.class, set(User_.GROUPS), field(Role_.GROUPS, UserGroup_.USERS)));
    roles = new TestEntities<>("actions", label, size, Role.class, newRole
    , new SelectQuery<>(em, Role.class, Role_.GROUPS)
    , check, om, em, tm) {
      @Override
      public void prePersist() {
        super.prePersist();
        for(int d = 1; d < data.size(); ++d) {
          for(int i = d; i < groups.data.size(); ++i) {
            data.get(d).addGroup(em.getReference(UserGroup.class, groups.data.get(i).getId()));
          }
        }
      }

      @Override
      public void preRemove(List<Role> toRemove) {
        for(Role item : toRemove) {
          item.removeGroups();
        }
      }
    };
    users = new TestEntities<>("users", label, size, User.class, newUser
    , new SelectQuery<>(em, User.class, User_.GROUPS)
    , check, om, em, tm) {
      @Override
      public void prePersist() {
        super.prePersist();
        for(int d = 1; d < data.size(); ++d) {
          for(int i = d; i < groups.data.size(); ++i) {
            data.get(d).addGroup(em.getReference(UserGroup.class, groups.data.get(i).getId()));
          }
        }
      }

      @Override
      public void preRemove(List<User> toRemove) {
        for(User item : toRemove) {
          item.removeGroups();
        }
      }
    };
    partners = new TestEntities<>(
      "partners", label, size, Partner.class, newPartner
    , new SelectQuery<>(em, Partner.class, PARTNER_FETCH_RELATIONS)
    , check, om, em, tm) {
      @Override
      public void prePersist() {
        super.prePersist();
        for(int d = 0; d < data.size(); ++d) {
          data.get(d == 0 ? 1 : d)
          .addUser(em.getReference(User.class, users.data.get(d).getId()));
          if(d > 0) {
            data.get(d).setLocationType(locationTypes.data.get(d));
          }
          if(d > 0) {
            data.get(d).setParent(data.get(d - 1));
          }
        }
      }

      @Override
      public void preRemove(List<Partner> toRemove) {
        for(Partner item : toRemove) {
          item.setUsers(null);
        }
      }
    };
    contacts = new TestEntities<>("contacts", label, size, Contact.class
    , newContact, new SelectQuery<>(em, Contact.class, Contact_.CONTACT_TYPE)
    , check, om, em, tm) {
      @Override
      public void prePersist() {
        super.prePersist();
        for(int d = 0; d < data.size(); ++d) {
          data.get(d).setContactType(em.getReference(ContactType.class
          , contactTypes.data.get(d).getId()));
          partners.data.get(d == 0 ? 1 : d).addContact(data.get(d));
        }
      }
    };
    contactDetails = new TestEntities<>("contactDetails", label, size, ContactDetails.class
    , newContactDetails, new SelectQuery<>(em, ContactDetails.class)
    , check, om, em, tm) {
      @Override
      public void prePersist() {
        super.prePersist();
        for(int d = 0; d < data.size(); ++d) {
          contacts.data.get(d).setContactDetails(data.get(d));
        }
      }
    };
    partnerDetails = new TestEntities<>("partnerDetails", label, 4, PartnerDetails.class
    , newPartnerDetails, new SelectQuery<>(em, PartnerDetails.class)
    , check, om, em, tm) {
      @Override
      public void prePersist() {
        super.prePersist();
        for(int d = 0; d < data.size(); ++d) {
          data.get(d).setId(partners.data.get(d).getId());
        }
      }
    };
    personDetails = new TestEntities<>("personDetails", label, size, PersonDetails.class
    , newPersonDetails, new SelectQuery<>(em, PersonDetails.class)
    , check, om, em, tm) {
      @Override
      public void prePersist() {
        super.prePersist();
        for(int d = 0; d < data.size(); ++d) {
          data.get(d).setId(partners.data.get(d).getId());
        }
      }
    };
    partnerFiles = new TestEntities<>("partnerFiles", label, size, PartnerFile.class
    , newPartnerFile, new SelectQuery<>(em, PartnerFile.class)
    , check, om, em, tm) {
      @Override
      public void prePersist() {
        super.prePersist();
        for(int d = 0; d < data.size(); ++d) {
          partners.data.get(d).addPartnerFile(data.get(d));
        }
      }
    };
    testEntities = new TestEntities[] {
      partnerFiles, personDetails, partnerDetails, contactDetails, contacts
    , partners, contactTypes, locationTypes, users, roles, groups
    };
  }

  @Override
  public void detach() {
    for(int i = testEntities.length; --i >= 0;) {
      testEntities[i].detach();
    }
  }

  @Override
  public void persist() {
    for(int i = testEntities.length; --i >= 0;) {
      testEntities[i].persist();
      em.flush();
      em.clear();
    }
  }

  @Override
  public void read() {
    for(int i = 0; i < testEntities.length; ++i) {
      testEntities[i].read();
    }
    em.flush();
    em.clear();
  }

  @Override
  public void remove() {
    for(int i = 0; i < testEntities.length; ++i) {
      testEntities[i].remove();
    }
  }

  @Override
  public void log() {
    for(int i = 0; i < testEntities.length; ++i) {
      final int it = i;
      log.info(() -> testEntities[it].title + "=" + Json.serializep(om, testEntities[it].data));
    }
  }

  //https://github.com/assertj/assertj/issues/2390
  //for [] <=> null
  public static void collectionsEmpty2null(Role unit) {
    if(CollectionUtils.isEmpty(unit.getGroups())) {
      unit.setGroups(null);
    } else {
      for(UserGroup item : unit.getGroups()) {
        if(CollectionUtils.isEmpty(item.getRoles())) {
          item.setRoles(null);
        }
      }
    }
  }

  public static void collectionsEmpty2null(Partner unit) {
    if(CollectionUtils.isEmpty(unit.getUsers())) {
      unit.setUsers(null);
    } else {
      for(User item : unit.getUsers()) {
        collectionsEmpty2null(item);
      }
    }
  }

  public static void collectionsEmpty2null(UserGroup unit) {
    if(CollectionUtils.isEmpty(unit.getUsers())) {
      unit.setUsers(null);
    } else {
      for(User item : unit.getUsers()) {
        collectionsEmpty2null(item);
      }
    }
    if(CollectionUtils.isEmpty(unit.getRoles())) {
      unit.setRoles(null);
    } else {
      for(Role item : unit.getRoles()) {
        collectionsEmpty2null(item);
      }
    }
  }

  public static void collectionsEmpty2null(User user) {
    if(CollectionUtils.isEmpty(user.getGroups())) {
      user.setGroups(null);
    } else {
      for(UserGroup item : user.getGroups()) {
        if(CollectionUtils.isEmpty(item.getRoles())) {
          item.setRoles(null);
        }
      }
    }
  }

}
