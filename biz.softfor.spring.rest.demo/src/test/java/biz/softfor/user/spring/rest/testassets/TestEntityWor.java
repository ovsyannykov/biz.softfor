package biz.softfor.user.spring.rest.testassets;

import biz.softfor.jpa.IdEntity;
import biz.softfor.util.Generated;
import com.fasterxml.jackson.annotation.JsonFilter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Generated("biz.softfor.user.spring.rest.testassets.TestEntity")
@Entity
@Table(name = TestEntity.TABLE)
@ToString(callSuper = true)
@Getter
@Setter
@JsonFilter("TestEntityWor")
public class TestEntityWor extends IdEntity<Integer> implements Serializable {

  public final static String EVERYBODY = "everybody";
  public final static String AUTHORIZED = "authorized";
  public final static String NOBODY = "nobody";

  @Column
  private String everybody;

  @Column
  private String authorized;

  @Column
  private String nobody;

  private final static long serialVersionUID = 0L;

}
