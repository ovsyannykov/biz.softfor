package biz.softfor.user.spring.rest;

import biz.softfor.user.api.UserRto;
import biz.softfor.user.spring.rest.testassets.TeztEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest(classes = { ConfigUserRest.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
@EntityScan(basePackageClasses = { TeztEntity.class })
public class InitAccessActionsTest {

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Test
  public void test() {
    UserRto[] users
    = { SecurityTest.ADMIN_DTO, DefaultAccessTest.DEFAULT_ACCESS_USER_DTO };
    System.out.println("insert into users(" + UserRto.PERSON_ID
    + ", " + UserRto.USERNAME + ", " + UserRto.PASSWORD
    + ", " + UserRto.EMAIL + ") values");
    for(int i = 0; i < users.length; ++i) {
      String d = i > 0 ? "," : " ";
      UserRto u = users[i];
      System.out.println(d + "(1,'" + u.getUsername() + "','"
      + passwordEncoder.encode(u.getPassword()) + "','" + u.getEmail() + "')--" + (i + 1));
    }
  }

}
