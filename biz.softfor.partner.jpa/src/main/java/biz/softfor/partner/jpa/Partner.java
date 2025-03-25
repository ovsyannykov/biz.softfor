package biz.softfor.partner.jpa;

import biz.softfor.address.jpa.Postcode;
import biz.softfor.jpa.IdEntity;
import biz.softfor.user.jpa.User;
import biz.softfor.util.partner.PartnerType;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = Partner.TABLE)
@Getter
@Setter
@ToString(callSuper = true)
@JsonFilter("Partner")
public class Partner extends IdEntity<Long> implements Serializable {

  public final static String TABLE = "partners";
  public final static String TITLE = "partner";
  public final static String namePerson = "namePerson";
  public final static String fullnamePerson = "fullnamePerson";
  public final static String regcodePerson = "regcodePerson";
  public final static String regdatePerson = "regdatePerson";
  public final static String nameNotPerson = "nameNotPerson";
  public final static String fullnameNotPerson = "fullnameNotPerson";
  public final static String regcodeNotPerson = "regcodeNotPerson";
  public final static String regdateNotPerson = "regdateNotPerson";
  public final static String Person_details_must_be_empty_for_the_non_person_partner_type = "Person_details_must_be_empty_for_the_non_person_partner_type";

  @Column
  @NotNull
  private PartnerType typ;

  /**person name, organization short name*/
  @Column
  @NotBlank
  @Size(max = 63)
  private String partnerName;

  @Column
  @Temporal(TemporalType.DATE)
  @NotNull
  @PastOrPresent
  private LocalDate partnerRegdate;

  @Column
  @NotBlank
  @Size(max = 23)
  private String partnerRegcode;

  @Column
  @NotBlank
  @Size(max = 255)
  private String address;

  /**person surname, organization full name*/
  @Column
  @NotBlank
  @Size(max = 255)
  private String partnerFullname;

  @OneToOne(optional = true, fetch = FetchType.LAZY)
  @PrimaryKeyJoinColumn
  private PartnerDetails partnerDetails;

  @OneToOne(orphanRemoval = true, optional = true, fetch = FetchType.LAZY)
  @PrimaryKeyJoinColumn
  private PersonDetails personDetails;

  @OneToMany(mappedBy = PARTNER_FILES_MAPPED_BY, orphanRemoval = true, fetch = FetchType.LAZY)
  @JsonIgnoreProperties(value = { PARTNER_FILES_MAPPED_BY }, allowSetters = true)
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private List<PartnerFile> partnerFiles;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "postcodeId")
  private Postcode postcode;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "locationTypeId")
  private LocationType locationType;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parentId")
  @JsonIgnoreProperties(value = { "parent" }, allowSetters = true)
  private Partner parent;

  @OneToMany(mappedBy = CONTACTS_MAPPED_BY, orphanRemoval = true, fetch = FetchType.LAZY)
  @JsonIgnoreProperties(value = { CONTACTS_MAPPED_BY }, allowSetters = true)
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private List<Contact> contacts;

  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "personId")
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private List<User> users;

  private final static String PARTNER_FILES_MAPPED_BY = "partner";
  private final static String CONTACTS_MAPPED_BY = "partner";
  private final static long serialVersionUID = 0L;

  @Override
  public void setId(Long id) {
    super.setId(id);
    if(partnerDetails != null) {
      partnerDetails.setId(id);
    }
    if(personDetails != null) {
      personDetails.setId(id);
    }
  }

  public void setPartnerDetails(PartnerDetails partnerDetails) {
    if(partnerDetails == null) {
      if(this.partnerDetails != null) {
        this.partnerDetails.setId(null);
      }
    } else {
      partnerDetails.setId(getId());
    }
    this.partnerDetails = partnerDetails;
  }

  public void setPersonDetails(PersonDetails personDetails) {
    if(personDetails == null) {
      if(this.personDetails != null) {
        this.personDetails.setId(null);
      }
    } else {
      personDetails.setId(getId());
    }
    this.personDetails = personDetails;
  }

  public void addPartnerFile(PartnerFile e) {
    e.setPartner(this);
    if(partnerFiles == null) {
      partnerFiles = new ArrayList<>();
    }
    partnerFiles.add(e);
  }

  public void removePartnerFile(PartnerFile e) {
    if(partnerFiles != null) {
      partnerFiles.remove(e);
    }
    e.setPartner(null);
  }

  @SuppressWarnings("empty-statement")
  public void removePartnerFiles() {
    if(partnerFiles != null) {
      for(Iterator<PartnerFile> i = partnerFiles.iterator(); i.hasNext();
      i.next().setPartner(null), i.remove());
    }
  }

  public void removePartnerFiles(Collection<PartnerFile> elems) {
    if(partnerFiles != null) {
      partnerFiles.removeAll(elems);
    }
    if(elems != null) {
      for(PartnerFile e : elems) {
        e.setPartner(null);
      }
    }
  }

  public void setPartnerFiles(List<PartnerFile> elems) {
    if(partnerFiles != elems) {
      removePartnerFiles();
      if(elems != null) {
        for(PartnerFile e : elems) {
          e.setPartner(this);
        }
      }
      partnerFiles = elems;
    }
  }

  public void addContact(Contact e) {
    e.setPartner(this);
    if(contacts == null) {
      contacts = new ArrayList<>();
    }
    contacts.add(e);
  }

  public void removeContact(Contact e) {
    if(contacts != null) {
      contacts.remove(e);
    }
    e.setPartner(null);
  }

  @SuppressWarnings("empty-statement")
  public void removeContacts() {
    if(contacts != null) {
      for(Iterator<Contact> i = contacts.iterator(); i.hasNext();
      i.next().setPartner(null), i.remove());
    }
  }

  public void removeContacts(Collection<Contact> elems) {
    if(contacts != null) {
      contacts.removeAll(elems);
    }
    if(elems != null) {
      for(Contact e : elems) {
        e.setPartner(null);
      }
    }
  }

  public void setContacts(List<Contact> elems) {
    if(contacts != elems) {
      removeContacts();
      if(elems != null) {
        for(Contact e : elems) {
          e.setPartner(this);
        }
      }
      contacts = elems;
    }
  }

  public void addUser(User e) {
    e.setPersonId(getId());
    if(users == null) {
      users = new ArrayList<>();
    }
    users.add(e);
  }

  public void removeUser(User e) {
    if(users != null) {
      users.remove(e);
    }
    e.setPersonId(null);
  }

  @SuppressWarnings("empty-statement")
  public void removeUsers() {
    if(users != null) {
      for(Iterator<User> i = users.iterator(); i.hasNext();
      i.next().setPersonId(null), i.remove());
    }
  }

  public void removeUsers(Collection<User> elems) {
    if(users != null) {
      users.removeAll(elems);
    }
    if(elems != null) {
      for(User e : elems) {
        e.setPersonId(null);
      }
    }
  }

  public void setUsers(List<User> elems) {
    if(users != elems) {
      removeUsers();
      if(elems != null) {
        for(User e : elems) {
          e.setPersonId(getId());
        }
      }
      users = elems;
    }
  }

  public String details() {
    String result;
    if(typ.isPerson) {
      result = partnerName;
      if(personDetails != null && personDetails.getMiddlename() != null) {
        result += " " + personDetails.getMiddlename();
      }
      result += " " + partnerFullname;
    } else {
      result = partnerFullname;
    }
    return result;
  }

  public String label() {
    String result = partnerName;
    if(typ.isPerson) {
      result += " " + partnerFullname;
    }
    return result;
  }

  @AssertTrue(message = Person_details_must_be_empty_for_the_non_person_partner_type)
  @JsonIgnore
  public boolean isPartnerTypeIsPersonOrPersonDetailsIsEmpty() {
    return getTyp() != null && getTyp().isPerson || getPersonDetails() == null;
  }

}
