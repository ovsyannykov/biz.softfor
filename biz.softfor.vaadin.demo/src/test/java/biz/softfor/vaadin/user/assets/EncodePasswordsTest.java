package biz.softfor.vaadin.user.assets;

import biz.softfor.vaadin.demo.App;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;

//@org.springframework.boot.test.context.SpringBootTest
@ContextConfiguration(classes = { App.class })
public class EncodePasswordsTest {

  @Autowired
  private PasswordEncoder passwordEncoder;

  //@org.junit.jupiter.api.Test
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
