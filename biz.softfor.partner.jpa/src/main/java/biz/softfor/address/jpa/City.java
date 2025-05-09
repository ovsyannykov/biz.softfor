package biz.softfor.address.jpa;

import biz.softfor.jpa.IdEntity;
import com.fasterxml.jackson.annotation.JsonFilter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = City.TABLE)
@Getter
@Setter
@ToString(callSuper = true)
@JsonFilter("City")
public class City extends IdEntity<Integer> implements Serializable {

  public final static String TABLE = "cities";
  public final static String TITLE = "city";

  @Column
  @NotBlank
  @Size(max = 127)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "typeId")
  @NotNull
  private CityType typ;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "stateId")
  @NotNull
  private State state;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "districtId")
  @NotNull
  private District district;

  private final static long serialVersionUID = 0L;

}
