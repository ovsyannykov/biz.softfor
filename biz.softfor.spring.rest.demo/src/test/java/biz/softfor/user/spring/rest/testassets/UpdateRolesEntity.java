package biz.softfor.user.spring.rest.testassets;

import biz.softfor.jpa.IdEntity;
import biz.softfor.jpa.crud.querygraph.TestEntity;
import com.fasterxml.jackson.annotation.JsonFilter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@TestEntity
@Entity
@Table(name = UpdateRolesEntity.TABLE)
@Getter
@Setter
@ToString(callSuper = true)
@JsonFilter("UpdateRolesEntity")
@NoArgsConstructor
public class UpdateRolesEntity extends IdEntity<Integer> implements Serializable {

  public final static String TABLE = "testentities";

  private final static long serialVersionUID = 0L;

  @Column
  private String everybody;

  @Column
  private String authorized;

  @Column
  private String nobody;

}
