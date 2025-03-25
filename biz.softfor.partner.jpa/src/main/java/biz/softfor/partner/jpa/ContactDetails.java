package biz.softfor.partner.jpa;

import biz.softfor.jpa.AssignableIdEntity;
import com.fasterxml.jackson.annotation.JsonFilter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = ContactDetails.TABLE)
@Getter
@Setter
@ToString(callSuper = true)
@JsonFilter("ContactDetails")
public class ContactDetails extends AssignableIdEntity<Long>
implements Serializable {

  public final static String TABLE = "contactDetails";

  @Column
  @Size(max = 255)
  private String note;

  private final static long serialVersionUID = 0L;

}
