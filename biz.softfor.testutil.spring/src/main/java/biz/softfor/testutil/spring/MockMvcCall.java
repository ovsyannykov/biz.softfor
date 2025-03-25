package biz.softfor.testutil.spring;

import biz.softfor.util.RequestUtil;
import biz.softfor.util.ServiceCall;
import biz.softfor.util.api.ServiceResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import lombok.extern.java.Log;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

@Log
public class MockMvcCall extends ServiceCall {

  private final MockMvc mockMvc;

  public MockMvcCall
  (MockMvc mockMvc, String rootPath, ObjectMapper objectMapper) {
    super(rootPath, 0, objectMapper);
    this.mockMvc = mockMvc;
  }

  @Override
  public ServiceResponse callWithStrBody
  (String httpMethod, String url, String params) throws Exception {
    log.info(
      () -> "url=" + url + "\nhttpMethod=" + httpMethod + "\nparams=" + params
    );
    MockHttpServletRequestBuilder requestBuilder
    = MockMvcRequestBuilders.request(httpMethod, new URI(url))
    .contentType(RequestUtil.JSON_CONTENT_TYPE)
    .content(params);
    MockHttpServletResponse response = mockMvc.perform(requestBuilder)
    .andDo(MockMvcResultHandlers.print()).andReturn().getResponse();
    return new ServiceResponse(response.getStatus(), response.getContentAsString());
  }

}
