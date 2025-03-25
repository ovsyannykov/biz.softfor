package biz.softfor.user.spring.ws;

import biz.softfor.spring.Constants;
import biz.softfor.spring.ws.WsdlUtil;
import biz.softfor.user.jpa.UserRequest;
import biz.softfor.user.spring.UserSvc;
import biz.softfor.util.api.CommonResponse;
import java.beans.IntrospectionException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.wsdl.wsdl11.Wsdl11Definition;
import org.springframework.xml.xsd.commons.CommonsXsdSchemaCollection;

@Endpoint
public class UserWs {

  public final static String LOCATION = "/";
  public final static String PORT_TYPE_NAME = "user";
  public final static String NAMESPACE_URI = "biz.softfor.user.api";
  public final static String userRead_REQUEST_LOCAL_NAME = "UserReadRequest";
  public final static String userRead_RESPONSE_LOCAL_NAME = "UserReadResponse";

  @Value(Constants.SERVER_PORT_VALUE)
  private int port;

  @Autowired
  private UserSvc service;

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = userRead_REQUEST_LOCAL_NAME)
	@ResponsePayload
  public UserReadResponse read(@RequestPayload UserReadRequest request)
  throws ReflectiveOperationException, IntrospectionException {
    UserRequest.Read svcRequest = new UserRequest.Read();
    svcRequest.filter.setId(new HashSet<>(request.getFilter().getId()));
    svcRequest.fields = request.getFields();
    CommonResponse<biz.softfor.user.jpa.User> svcResponse = service.read(svcRequest);
    biz.softfor.user.jpa.User svcUser = svcResponse.getData(0);
    User user = new User();
    user.setId(svcUser.getId());
    user.setUsername(svcUser.getUsername());
    user.setPassword(svcUser.getPassword());
    user.setEmail(svcUser.getEmail());
    user.setPersonId(svcUser.getPersonId());
    Set<biz.softfor.user.jpa.UserGroup> svcGroups = svcUser.getGroups();
    if(svcGroups != null) {
      List<UserGroup> groups = user.getGroups();
      for(biz.softfor.user.jpa.UserGroup svcGroup : svcGroups) {
        UserGroup group = new UserGroup();
        group.setId(svcGroup.getId());
        group.setName(svcGroup.getName());
        groups.add(group);
      }
    }
    UserReadResponse result = new UserReadResponse();
    result.getData().add(user);
    return result;
  }

  @Bean
  //StdPath.locationUri(port) + wsRootPath + "/wsdl/" + Wsdl11Definition_BEAN_NAME + ".wsdl"
  //http://localhost:8080/ws/wsdl/userApi.wsdl
  public Wsdl11Definition userApi(CommonsXsdSchemaCollection xsds) throws Exception {
    return WsdlUtil.wsdl11Definition(xsds, LOCATION, PORT_TYPE_NAME, NAMESPACE_URI);
  }

}
