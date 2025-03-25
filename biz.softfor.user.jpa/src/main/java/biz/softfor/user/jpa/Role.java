package biz.softfor.user.jpa;

import biz.softfor.util.api.Identifiable;
import biz.softfor.util.security.AbstractRoleCalc;
import biz.softfor.util.security.DefaultAccess;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

//natural id: (objName, updateFor)
//editable: name, description, defaultAccess, disabled
//read-only: orphan, deniedForAll
@NoArgsConstructor
@Entity
@Table(name = Role.TABLE)
@Getter
@Setter
@ToString
@JsonFilter("Role")
public class Role implements Identifiable<Long>, Serializable {

  public final static String TABLE = "roles";
  public final static String TITLE = "role";
  public final static String Delete_constraint = "Delete_constraint";

  @Id
  private Long id;

  @Column
  @NotNull
  private DefaultAccess defaultAccess;

  @Column
  private Boolean isUrl;

  @Column
  private Boolean updateFor;

  @Column
  @NotNull
  private Boolean disabled;

  @Column
  private Boolean orphan;

  @Column
  private Boolean deniedForAll;

  @Column
  private String objName;

  @Column
  @NotBlank
  @Size(min = 2, max = 127)
  private String name;

  @Column
  @NotBlank
  @Size(min = 2, max = 511)
  private String description;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "roles_groups", joinColumns = @JoinColumn(name = "roleId"), inverseJoinColumns = @JoinColumn(name = "groupId"))
  @JsonIgnoreProperties(value = { UserGroup.ROLES }, allowSetters = true)
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private Set<UserGroup> groups;
  public final static String GROUPS = "groups";

  public void addGroup(UserGroup v) {
    if(v != null) {
      Set<Role> items = v.getRoles();
      if(items == null) {
        items = new HashSet<>();
        v.setRoles(items);
      }
      items.add(this);
      if(groups == null) {
        groups = new HashSet<>();
      }
      groups.add(v);
    }
  }

  public void removeGroup(UserGroup v) {
    if(v != null) {
      Set<Role> items = v.getRoles();
      if(items != null) {
        items.remove(this);
      }
      if(groups != null) {
        groups.remove(v);
      }
    }
  }

  @SuppressWarnings("empty-statement")
  public void removeGroups() {
    if(groups != null) {
      for(Iterator<UserGroup> i = groups.iterator(); i.hasNext();) {
        Set<Role> items = i.next().getRoles();
        if(items != null) {
          items.remove(this);
        }
        i.remove();
      }
    }
  }

  public void setGroups(Set<UserGroup> items) {
    if(groups != items) {
      removeGroups();
      if(items != null) {
        for(UserGroup v : items) {
          v.addRole(this);
        }
      }
      groups = items;
    }
  }

  private final static long serialVersionUID = 0L;

  public Role(AbstractRoleCalc calc) {
    id = calc.id();
    defaultAccess = calc.defaultAccess();
    isUrl = calc.isUrl();
    updateFor = calc.updateFor;
    disabled = false;
    orphan = false;
    deniedForAll = calc.deniedForAll();
    objName = calc.objName();
    name = calc.name();
    description = calc.description();
  }

  @Override
  @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
  public boolean equals(Object a) {
    return Identifiable.equals(this, a);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

}
