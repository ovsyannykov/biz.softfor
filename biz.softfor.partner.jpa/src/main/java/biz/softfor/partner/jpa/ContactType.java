package biz.softfor.partner.jpa;

import biz.softfor.jpa.IdEntity;
import com.fasterxml.jackson.annotation.JsonFilter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = ContactType.TABLE)
@Getter
@Setter
@ToString(callSuper = true)
@JsonFilter("ContactType")
public class ContactType extends IdEntity<Short> implements Serializable {

  public final static String TABLE = "contactTypes";
  public final static String TITLE = "contactType";

  @Column
  @NotBlank
  @Size(min = 2, max = 63)
  private String name;

  @Column
  @NotBlank
  @Size(min = 2, max = 255)
  private String descr;

  private final static long serialVersionUID = 0L;

}
