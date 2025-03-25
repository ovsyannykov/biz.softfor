package biz.softfor.user.spring.ws;

import biz.softfor.spring.ws.ConfigWsServer;
import biz.softfor.spring.ws.demo.App;
import biz.softfor.util.api.StdPath;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.ClassUtils;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.xml.transform.StringResult;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = { App.class })
public class UserWsTest {

  @LocalServerPort
  private int port;

  @Value(ConfigWsServer.WS_ROOT_VALUE)
  private String wsRootPath;

  private final static Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

  @BeforeAll
  public static void beforeAll() throws Exception {
    marshaller.setPackagesToScan(ClassUtils.getPackageName(UserReadRequest.class));
    marshaller.afterPropertiesSet();
  }

  @Test
  public void readById() throws Exception {
		WebServiceTemplate ws = new WebServiceTemplate(marshaller);
    UserReadRequest readReq = new UserReadRequest();
    UserFilter userFilter = new UserFilter();
    userFilter.getId().add(51L);
    readReq.setFilter(userFilter);
    readReq.getFields().add("username");
    readReq.getFields().add("email");
    readReq.getFields().add("groups");

    StringResult requestSr = new StringResult();
    marshaller.marshal(readReq, requestSr);
    System.out.println(requestSr.toString());

    UserReadResponse response = (UserReadResponse)ws.marshalSendAndReceive(StdPath.locationUri(port) + wsRootPath, readReq);

    StringResult responseSr = new StringResult();
    marshaller.marshal(response, responseSr);
    System.out.println(responseSr.toString());

    assertThat(response.getData().size()).isEqualTo(1);
		assertThat(response.getData().get(0).getUsername()).isEqualTo("sadm");
  }

}
