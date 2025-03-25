package biz.softfor.partner.jpa;

import biz.softfor.jpa.SetStoredEntity;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = Contact.TABLE)
@Getter
@Setter
@ToString(callSuper = true)
@JsonFilter("Contact")
public class Contact extends SetStoredEntity<Long> implements Serializable {

  public final static String TABLE = "contacts";
  public final static String TITLE = "contact";

  @Column
  @NotBlank
  @Size(min = 2, max = 63)
  private String descr;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "partnerId")
  @JsonIgnoreProperties(value = { "contacts" }, allowSetters = true)
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @NotNull
  private Partner partner;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "appointmentId")
  private Appointment appointment;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "typeId")
  @NotNull
  private ContactType contactType;

  @Column
  @NotNull
  private Boolean isPublic;

  @OneToOne(optional = true, orphanRemoval = true, fetch = FetchType.LAZY)
  @PrimaryKeyJoinColumn
  @Valid
  private ContactDetails contactDetails;

  private final static long serialVersionUID = 0L;

  public void setContactDetails(ContactDetails contactDetails) {
    if(contactDetails == null) {
      if(this.contactDetails != null) {
        this.contactDetails.setId(null);
      }
    } else {
      contactDetails.setId(getId());
    }
    this.contactDetails = contactDetails;
  }

}
