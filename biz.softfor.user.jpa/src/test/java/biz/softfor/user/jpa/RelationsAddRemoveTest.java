package biz.softfor.user.jpa;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

public class RelationsAddRemoveTest {

  @Test
  public void manyToMany() {
    Role[] roles = new Role[4];
    for(int a = 0; a < roles.length; ++a) {
      roles[a] = new Role();
      roles[a].setName("a" + a);
    }
    UserGroup[] groups = new UserGroup[4];
    for(int r = 0; r < groups.length; ++r) {
      groups[r] = new UserGroup();
      groups[r].setName("r" + r);
    }

    for(int a = 0; a < roles.length; ++a) {
      for(int r = 0; r <= a; ++r) {
        roles[a].addGroup(groups[r]);
      }
    }
    for(int r = 1; r < groups.length; ++r) {
      assertThat(groups[r].getRoles().size())
      .as("groups[" + r + "].getRoles().size()=" + groups[r].getRoles().size())
      .isEqualTo(groups.length - r);
    }

    groups[0].removeRoles();
    assertThat(groups[0].getRoles())
    .as("groups[0].getRoles()=" + groups[0].getRoles())
    .isEmpty();
    for(int a = 0; a < roles.length; ++a) {
      assertThat(roles[a].getGroups())
      .as("roles[" + a + "].getGroups()=" + roles[a].getGroups())
      .doesNotContain(groups[0]);
    }

    Set<Role> rolesSet = new HashSet<>(roles.length);
    Collections.addAll(rolesSet, roles);
    groups[0].setRoles(rolesSet);
    for(int a = 0; a < roles.length; ++a) {
      assertThat(roles[a].getGroups())
      .as("roles[" + a + "].getGroups()=" + roles[a].getGroups())
      .contains(groups[0]);
    }

    for(int a = 0; a < roles.length; ++a) {
      roles[a].removeGroup(groups[0]);
    }
    for(int a = 0; a < roles.length; ++a) {
      assertThat(roles[a].getGroups().size())
      .as("roles[" + a + "].getGroups().size()=" + roles[a].getGroups().size())
      .isEqualTo(a);
      assertThat(roles[a].getGroups())
      .as("roles[" + a + "].getGroups()=" + roles[a].getGroups())
      .doesNotContain(groups[0]);
    }

    roles[0].setGroups(null);
    assertThat(roles[0].getGroups())
    .as("roles[0].getGroups()=" + roles[0].getGroups())
    .isNull();
  }

}
