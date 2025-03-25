package biz.softfor.util.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonIgnoreProperties({ CommonResponse.IS_EMPTY_FOR_UPDATE })
public class CommonResponse<T> extends BasicResponse<List<T>> {

  private long total;
  public final static String TOTAL = "total";

  public final static String IS_EMPTY_FOR_UPDATE = "isEmptyForUpdate";

  public long getTotal() {
    return total;
  }

  public void setTotal(long total) {
    this.total = total;
  }

  public CommonResponse(int status, String descr, List<T> data, long total) {
    super(status, descr, data);
    this.total = total;
  }

  public CommonResponse() {
    this(OK, OK_DESCR, new ArrayList<>(), 0);
  }

  public CommonResponse(int status, String descr) {
    this(status, descr, new ArrayList<>(), 0);
  }

  public CommonResponse(List<T> data, long total) {
    this(OK, OK_DESCR, data, total);
  }

  public CommonResponse(long total) {
    this(OK, OK_DESCR, new ArrayList<>(), total);
  }

  public T getData(int i) {
    return getData().get(i);
  }

}
