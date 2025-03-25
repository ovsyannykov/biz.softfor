package biz.softfor.jpa.crud.querygraph;

import biz.softfor.jpa.crud.querygraph.ManyToManyInf;
import biz.softfor.user.jpa.Role;
import biz.softfor.user.jpa.Role_;
import biz.softfor.user.jpa.User;
import biz.softfor.user.jpa.UserGroup;
import biz.softfor.user.jpa.UserGroup_;
import biz.softfor.user.jpa.User_;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lombok.extern.java.Log;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Log
public class ManyToManyInfTest {

  private static record InOut(Class<?> inClass, String in, Class<?> outClass, String out) {}

  private static Stream<InOut> mappedField() {
    List<InOut> p = new ArrayList<>();
    p.add(new InOut(Role.class, Role_.GROUPS, UserGroup.class, UserGroup_.ROLES));
    p.add(new InOut(User.class, User_.GROUPS, UserGroup.class, UserGroup_.USERS));
    p.add(new InOut(UserGroup.class, UserGroup_.USERS, User.class, User_.GROUPS));
    p.add(new InOut(UserGroup.class, UserGroup_.ROLES, Role.class, Role_.GROUPS));
    return p.stream()
    //.skip(0).limit(1)
    ;
  }

  @ParameterizedTest
  @MethodSource
  public void mappedField(InOut p) throws ReflectiveOperationException {
    Field inField = p.inClass.getDeclaredField(p.in);
    Field mappedField = ManyToManyInf.mappedField(inField);
    assertThat(mappedField).as("mappedField").isNotNull();
    assertThat(mappedField).as("mappedField").isEqualTo(p.outClass.getDeclaredField(p.out));
  }

}
