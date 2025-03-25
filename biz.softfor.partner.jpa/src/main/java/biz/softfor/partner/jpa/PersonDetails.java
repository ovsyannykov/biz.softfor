package biz.softfor.partner.jpa;

import biz.softfor.jpa.AssignableIdEntity;
import com.fasterxml.jackson.annotation.JsonFilter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = PersonDetails.TABLE)
@Getter
@Setter
@ToString(callSuper = true)
@JsonFilter("PersonDetails")
public class PersonDetails extends AssignableIdEntity<Long>
implements Serializable {

  public final static String TABLE = "personDetails";

  @Column
  @NotBlank
  @Size(min = 2, max = 11)
  private String passportSeries;

  @Column
  @NotNull
  private Integer passportNumber;

  @Column
  @Temporal(TemporalType.DATE)
  @NotNull
  @PastOrPresent
  private LocalDate passportDate;

  @Column
  @NotBlank
  @Size(min = 2, max = 63)
  private String middlename;

  @Column
  @NotBlank
  @Size(min = 2, max = 255)
  private String passportIssued;

  @Column
  private Boolean married;

  private final static long serialVersionUID = 0L;

}
