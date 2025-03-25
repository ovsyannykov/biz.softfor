package biz.softfor.util.api;

import java.util.List;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AuthResponse extends CommonResponse<TokenInfo> {

  public final static int ACCESS_TOKEN_ID = 0;
  public final static int REFRESH_TOKEN_ID = 1;

  public AuthResponse(List<TokenInfo> data) {
    super(data, data.size());
  }

  public AuthResponse(int status, String descr) {
    super(status, descr);
  }

}
