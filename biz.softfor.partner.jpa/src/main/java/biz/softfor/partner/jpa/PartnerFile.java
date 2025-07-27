package biz.softfor.partner.jpa;

import biz.softfor.jpa.IdEntity;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
import org.hibernate.validator.constraints.URL;

@Entity
@Table(name = PartnerFile.TABLE)
@Getter
@Setter
@ToString(callSuper = true)
@JsonFilter("PartnerFile")
public class PartnerFile extends IdEntity<Long> implements Serializable {

  public final static String TABLE = "partnerFiles";
  public final static String TITLE = "partnerFile";

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "partnerId")
  @JsonIgnoreProperties(value = { "partnerFiles" }, allowSetters = true)
  @ToString.Exclude
  @NotNull
  private Partner partner;

  @Column
  @NotBlank
  @Size(max = 255)
  private String descr;

  @Column
  @NotBlank
  @Size(max = 511)
  private String uri;

  private final static long serialVersionUID = 0L;

}
