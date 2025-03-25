package biz.softfor.spring.ws.client.demo;

import biz.softfor.user.ws.client.UserFilter;
import biz.softfor.user.ws.client.UserReadRequest;
import biz.softfor.user.ws.client.UserReadResponse;
import biz.softfor.util.Constants;
import biz.softfor.util.api.StdPath;
import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.util.ClassUtils;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.xml.transform.StringResult;

public class App {

  private final static String rootPath = StdPath.locationUri(Constants.SERVER_PORT_DEFAULT);

  public static void main(String[] args) throws Exception {
    Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
    marshaller.setPackagesToScan(ClassUtils.getPackageName(UserReadRequest.class));
    marshaller.afterPropertiesSet();

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

    UserReadResponse response = (UserReadResponse)ws.marshalSendAndReceive(rootPath + Constants.WS_ROOT_DEFAULT, readReq);

    StringResult responseSr = new StringResult();
    marshaller.marshal(response, responseSr);
    System.out.println(responseSr.toString());

    assertThat(response.getData().size()).isEqualTo(1);
		assertThat(response.getData().get(0).getUsername()).isEqualTo("sadm");
  }

}
