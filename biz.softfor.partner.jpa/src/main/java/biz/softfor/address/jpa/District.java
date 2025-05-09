package biz.softfor.address.jpa;

import biz.softfor.jpa.IdEntity;
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
@Table(name = District.TABLE)
@Getter
@Setter
@ToString(callSuper = true)
@JsonFilter("District")
public class District extends IdEntity<Integer> implements Serializable {

  public final static String TABLE = "districts";
  public final static String TITLE = "district";

  @Column
  @NotBlank
  @Size(min = 2, max = 63)
  private String name;

  @Column
  @NotBlank
  @Size(min = 2, max = 255)
  private String fullname;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "stateId")
  @NotNull
  @Valid
  private State state;

  private final static long serialVersionUID = 0L;

}
