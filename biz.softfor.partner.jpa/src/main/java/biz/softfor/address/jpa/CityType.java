package biz.softfor.address.jpa;

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
@Table(name = CityType.TABLE)
@Getter
@Setter
@ToString(callSuper = true)
@JsonFilter("CityType")
public class CityType extends IdEntity<Short> implements Serializable {

  public final static String TABLE = "cityTypes";
  public final static String TITLE = "cityType";

  @Column
  @NotBlank
  @Size(max = 8)
  private String name;

  @Column
  @NotBlank
  @Size(max = 47)
  private String fullname;

  private final static long serialVersionUID = 0L;

}
