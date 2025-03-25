package biz.softfor.vaadin.user;

import biz.softfor.vaadin.demo.App;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;

@Disabled
@SpringBootTest
@ContextConfiguration(classes = { App.class })
public class EncodePasswordsTest {

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Test
  public void encodePasswords() throws Exception {
    String[] data = { "admin", "manager", "user" };
    String res = "";
    for(int i = 0; i < data.length; ++i) {
      String delim = res.isEmpty() ? " " : ",";
      res += delim;
      res += "('" + data[i] + "@t.co','" + data[i] + "','" + passwordEncoder.encode(data[i]) + "')--" + (i + 1) + "\n";
    }
    System.out.println(res);
  }

}
