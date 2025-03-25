package biz.softfor.user.spring.rest;

import biz.softfor.user.api.UserDto;
import biz.softfor.user.spring.rest.testassets.TestEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest(classes = { ConfigUserRest.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
@EntityScan(basePackageClasses = { TestEntity.class })
public class InitAccessActionsTest {

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Test
  public void test() {
    UserDto[] users
    = { SecurityTest.ADMIN_DTO, DefaultAccessTest.DEFAULT_ACCESS_USER_DTO };
    System.out.println("insert into users(" + UserDto.PERSON_ID
    + ", " + UserDto.USERNAME + ", " + UserDto.PASSWORD
    + ", " + UserDto.EMAIL + ") values");
    for(int i = 0; i < users.length; ++i) {
      String d = i > 0 ? "," : " ";
      UserDto u = users[i];
      System.out.println(d + "(1,'" + u.getUsername() + "','"
      + passwordEncoder.encode(u.getPassword()) + "','" + u.getEmail() + "')--" + (i + 1));
    }
  }

}
