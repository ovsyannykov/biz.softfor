package biz.softfor.user.jpa;

import biz.softfor.jpa.IdEntity;
import com.fasterxml.jackson.annotation.JsonFilter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = Token.TABLE)
@Getter
@Setter
@ToString(callSuper = true)
@JsonFilter("Token")
public class Token extends IdEntity<Long> implements Serializable {

  public final static String TABLE = "tokens";
  public final static String TITLE = "token";
  public final static String GROUPS_DELIMITER = ",";

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "userId")
  @NotNull
  private User user;

  @Column
  @NotNull
  private Boolean isRefresh;

  @Column
  @Temporal(TemporalType.TIMESTAMP)
  @NotNull
  private LocalDateTime expired;

  @Column
  @NotNull
  @Size(max = 2047)
  private String groups;

  private final static long serialVersionUID = 0L;

}
