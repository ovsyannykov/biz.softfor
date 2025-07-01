package biz.softfor.user.spring;

import biz.softfor.address.jpa.City;
import biz.softfor.address.jpa.Country;
import biz.softfor.address.jpa.District;
import biz.softfor.address.jpa.District_;
import biz.softfor.address.jpa.State;
import biz.softfor.address.jpa.State_;
import biz.softfor.spring.jpa.crud.ConfigJpaCrud;
import biz.softfor.spring.jpa.properties.ConfigJpaProperties;
import biz.softfor.spring.messagesi18n.ConfigSpringMessagesI18n;
import biz.softfor.spring.objectmapper.ConfigObjectMapper;
import biz.softfor.spring.sqllog.ConfigSqlLog;
import biz.softfor.user.jpa.User;
import biz.softfor.user.jpa.User_;
import biz.softfor.util.security.AbstractRoleCalc;
import biz.softfor.util.security.ClassRoleCalc;
import biz.softfor.util.security.FieldRoleCalc;
import biz.softfor.util.security.UpdateFieldRoleCalc;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@DataJpaTest(showSql = false)
@EnableAutoConfiguration
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {
  SecurityMgrTest.class
, ConfigJpaCrud.class
, ConfigJpaProperties.class
, ConfigObjectMapper.class
, ConfigSqlLog.class
, ConfigSpringMessagesI18n.class
, ConfigUserSpring.class
, LocalValidatorFactoryBean.class
})
@EntityScan(basePackageClasses = { City.class })
public class SecurityMgrTest {

  @Autowired
  SecurityMgr securityMgr;

  private final static String MANAGER_GROUP_NAME = "MANAGER";
  private final static List<String> MANAGER_GROUP = List.of(MANAGER_GROUP_NAME);

  public static record Param(String label, AbstractRoleCalc roleCalc) {}

  public static Stream<Param> isDenied() throws NoSuchFieldException {
    List<Param> p = new ArrayList<>();
    p.add(new Param(
      "Simple field by type"
    , new UpdateFieldRoleCalc(User.class.getDeclaredField(User_.EMAIL))
    ));
    p.add(new Param(
      "Relation type by members"
    , new ClassRoleCalc(Country.class)
    ));
    p.add(new Param(
      "Relation field by members of its type"
    , new FieldRoleCalc(State.class.getDeclaredField(State_.COUNTRY))
    ));
    p.add(new Param(
      "Relation field by members including a relation field by members of its type"
    , new FieldRoleCalc(District.class.getDeclaredField(District_.STATE))
    ));
    return p.stream()
    //.skip(0).limit(1)
    ;
  }

  @ParameterizedTest
  @MethodSource
  public void isDenied(Param p) throws Exception {
    System.out.println("\n" + p.label);
    System.out.println("role=" + p.roleCalc.id() + " (" + p.roleCalc.description() + ")");
    boolean isAllowed = securityMgr.isAllowed(p.roleCalc.id(), MANAGER_GROUP);
    System.out.println("isAllowed=" + isAllowed);
    Assertions.assertThat(isAllowed).as("Access to " + p.roleCalc.description())
    .isFalse();
  }

  @Test
  public void isAllowedForNotLimitedClassWithNotLimitedField() throws Exception {
    AbstractRoleCalc roleCalc = new ClassRoleCalc(District.class);
    System.out.println("\nIs allowed for not restricted class with not restricted field");
    System.out.println("role=" + roleCalc.id() + " (" + roleCalc.description() + ")");
    boolean isAllowed = securityMgr.isAllowed(roleCalc.id(), Collections.EMPTY_LIST);
    System.out.println("isAllowed=" + isAllowed);
    Assertions.assertThat(isAllowed).as("Access to " + roleCalc.description())
    .isTrue();
  }

}
