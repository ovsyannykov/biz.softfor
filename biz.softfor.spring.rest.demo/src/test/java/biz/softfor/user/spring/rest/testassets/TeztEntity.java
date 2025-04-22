package biz.softfor.user.spring.rest.testassets;

import biz.softfor.jpa.IdEntity;
import biz.softfor.util.security.ActionAccess;
import biz.softfor.util.security.DefaultAccess;
import biz.softfor.util.security.UpdateAccess;
import com.fasterxml.jackson.annotation.JsonFilter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = TeztEntity.TABLE)
@Getter
@Setter
@ToString(callSuper = true)
@JsonFilter("TeztEntity")
@NoArgsConstructor
public class TeztEntity extends IdEntity<Integer> implements Serializable {

  public final static String TABLE = "testentities";

  private final static long serialVersionUID = 0L;

  @ActionAccess(defaultAccess = DefaultAccess.EVERYBODY)
  @UpdateAccess(defaultAccess = DefaultAccess.AUTHORIZED)
  @Column
  @NotBlank
  @Size(max = 15)
  private String everybody;

  @ActionAccess(defaultAccess = DefaultAccess.AUTHORIZED)
  @UpdateAccess(defaultAccess = DefaultAccess.NOBODY)
  @Column
  @NotBlank
  @Size(max = 15)
  private String authorized;

  @ActionAccess(defaultAccess = DefaultAccess.NOBODY)
  @UpdateAccess(defaultAccess = DefaultAccess.EVERYBODY)
  @Column
  @NotBlank
  @Size(max = 15)
  private String nobody;

}
