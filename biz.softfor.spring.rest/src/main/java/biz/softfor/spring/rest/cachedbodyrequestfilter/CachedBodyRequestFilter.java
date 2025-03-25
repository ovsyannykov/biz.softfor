package biz.softfor.spring.rest.cachedbodyrequestfilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class CachedBodyRequestFilter extends OncePerRequestFilter {

  private static class CachedBodyServletInputStream extends ServletInputStream {

    private final InputStream cachedBodyInputStream;

    public CachedBodyServletInputStream(byte[] cachedBody) {
      cachedBodyInputStream = new ByteArrayInputStream(cachedBody);
    }

    @Override
    public int read() throws IOException {
      return cachedBodyInputStream.read();
    }

    @Override
    public boolean isFinished() {
      return true;
    }

    @Override
    public boolean isReady() {
      return true;
    }

    @Override
    public void setReadListener(ReadListener readListener) {
    }

  }

  private static class CachedBodyHttpServletRequest
  extends HttpServletRequestWrapper {

    private final byte[] cachedBody;

    public CachedBodyHttpServletRequest(HttpServletRequest request)
    throws IOException {
      super(request);
      cachedBody = StreamUtils.copyToByteArray(request.getInputStream());
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
      return new CachedBodyServletInputStream(this.cachedBody);
    }

    @Override
    public BufferedReader getReader() throws IOException {
      return new BufferedReader(new InputStreamReader(
        new ByteArrayInputStream(this.cachedBody)
      ));
    }

  }

  @Override
  protected void doFilterInternal(
    HttpServletRequest request
  , HttpServletResponse response
  , FilterChain filterChain
  ) throws ServletException, IOException {
    filterChain.doFilter(new CachedBodyHttpServletRequest(request), response);
  }

}
