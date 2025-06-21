package biz.softfor.spring.ws;

import biz.softfor.util.Constants;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.ws.config.annotation.WsConfigurer;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.server.endpoint.adapter.method.MethodArgumentResolver;
import org.springframework.ws.server.endpoint.adapter.method.MethodReturnValueHandler;
import org.springframework.ws.server.endpoint.interceptor.PayloadLoggingInterceptor;
import org.springframework.ws.soap.server.endpoint.interceptor.PayloadValidatingInterceptor;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.xml.xsd.commons.CommonsXsdSchemaCollection;

@Configuration
public class ConfigWsServer implements WsConfigurer {

  public final static String WS_ROOT_VALUE = "${biz.softfor.ws.root:" + Constants.WS_ROOT_DEFAULT + "}";

  @Value(WS_ROOT_VALUE)
  private String wsRootPath;

  @Value("${biz.softfor.ws.schemas}")
  private String[] schemas;

  @Override
  public void addInterceptors(List<EndpointInterceptor> interceptors) {
    PayloadValidatingInterceptor validatingInterceptor = new PayloadValidatingInterceptor();
    validatingInterceptor.setValidateRequest(true);
    validatingInterceptor.setValidateResponse(true);
    validatingInterceptor.setXsdSchemaCollection(xsds());
    interceptors.add(validatingInterceptor);
    interceptors.add(new PayloadLoggingInterceptor());
  }

	@Bean
	public CommonsXsdSchemaCollection xsds() {
    Resource[] resources = new Resource[schemas.length];
    for(int i = schemas.length; --i >= 0;) {
      resources[i] = new ClassPathResource(schemas[i]);
    }
    CommonsXsdSchemaCollection result = new CommonsXsdSchemaCollection(resources);
    result.setInline(true);
		return result;
	}

  @Bean
  public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext applicationContext) {
    MessageDispatcherServlet result = new MessageDispatcherServlet();
    result.setApplicationContext(applicationContext);
    result.setTransformWsdlLocations(true);
    return new ServletRegistrationBean<>(result, wsRootPath + "/*");
  }

  @Override
  public void addArgumentResolvers(List<MethodArgumentResolver> argumentResolvers) {
  }

  @Override
  public void addReturnValueHandlers(List<MethodReturnValueHandler> returnValueHandlers) {
  }

}
