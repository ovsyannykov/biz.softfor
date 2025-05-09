package biz.softfor.user.jpa;

import biz.softfor.jpa.IdEntity;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = UserGroup.TABLE)
@Getter
@Setter
@ToString(callSuper = true)
@JsonFilter("UserGroup")
public class UserGroup extends IdEntity<Integer> implements Serializable {

  public final static String TABLE = "userGroups";
  public final static String TITLE = "userGroup";

  @Column
  @NotBlank
  @Size(min = 2, max = 63)
  private String name;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "users_groups", joinColumns = @JoinColumn(name = "groupId"), inverseJoinColumns = @JoinColumn(name = "userId"))
  @JsonIgnoreProperties(value = { User.GROUPS }, allowSetters = true)
  @ToString.Exclude
  private Set<User> users;
  public final static String USERS = "users";

  public void addUser(User v) {
    if(v != null) {
      Set<UserGroup> items = v.getGroups();
      if(items == null) {
        items = new HashSet<>();
        v.setGroups(items);
      }
      items.add(this);
      if(users == null) {
        users = new HashSet<>();
      }
      users.add(v);
    }
  }

  public void removeUser(User v) {
    if(v != null) {
      Set<UserGroup> items = v.getGroups();
      if(items != null) {
        items.remove(this);
      }
      if(users != null) {
        users.remove(v);
      }
    }
  }

  public void removeUsers() {
    if(users != null) {
      for(Iterator<User> i = users.iterator(); i.hasNext();) {
        Set<UserGroup> items = i.next().getGroups();
        if(items != null) {
          items.remove(this);
        }
        i.remove();
      }
    }
  }

  public void setUsers(Set<User> items) {
    if(users != items) {
      removeUsers();
      if(items != null) {
        for(User v : items) {
          v.addGroup(this);
        }
      }
      users = items;
    }
  }

  @ManyToMany(mappedBy = Role.GROUPS, fetch = FetchType.LAZY)
  @JsonIgnoreProperties(value = { Role.GROUPS }, allowSetters = true)
  @ToString.Exclude
  private Set<Role> roles;
  public final static String ROLES = "roles";

  public void addRole(Role v) {
    if(v != null) {
      Set<UserGroup> items = v.getGroups();
      if(items == null) {
        items = new HashSet<>();
        v.setGroups(items);
      }
      items.add(this);
      if(roles == null) {
        roles = new HashSet<>();
      }
      roles.add(v);
    }
  }

  public void removeRole(Role v) {
    if(v != null) {
      Set<UserGroup> items = v.getGroups();
      if(items != null) {
        items.remove(this);
      }
      if(roles != null) {
        roles.remove(v);
      }
    }
  }

  public void removeRoles() {
    if(roles != null) {
      for(Iterator<Role> i = roles.iterator(); i.hasNext();) {
        Set<UserGroup> items = i.next().getGroups();
        if(items != null) {
          items.remove(this);
        }
        i.remove();
      }
    }
  }

  public void setRoles(Set<Role> items) {
    if(roles != items) {
      removeRoles();
      if(items != null) {
        for(Role v : items) {
          v.addGroup(this);
        }
      }
      roles = items;
    }
  }

  private final static long serialVersionUID = 0L;

}
