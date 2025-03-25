package biz.softfor.address.jpa;

import biz.softfor.jpa.SetStoredEntity;
import com.fasterxml.jackson.annotation.JsonFilter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = Postcode.TABLE)
@Getter
@Setter
@ToString(callSuper = true)
@JsonFilter("Postcode")
public class Postcode extends SetStoredEntity<Integer> implements Serializable {

  public final static String TABLE = "postcodes";
  public final static String TITLE = "postcode";

  @Column
  @NotBlank
  @Size(min = 2, max = 11)
  private String postcode;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "districtId")
  @NotNull
  @Valid
  private District district;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "cityId")
  @NotNull
  @Valid
  private City city;

  private final static long serialVersionUID = 0L;

}
