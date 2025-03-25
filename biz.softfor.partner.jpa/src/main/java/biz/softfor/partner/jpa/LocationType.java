package biz.softfor.partner.jpa;

import biz.softfor.jpa.SetStoredEntity;
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
@Table(name = LocationType.TABLE)
@Getter
@Setter
@ToString(callSuper = true)
@JsonFilter("LocationType")
public class LocationType extends SetStoredEntity<Short> implements Serializable {

  public final static String TABLE = "locationTypes";
  public final static String TITLE = "locationType";

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
