package biz.softfor.util.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class InternalResponse<T> extends CommonResponse<T> {

  @JsonIgnore
  private boolean isEmptyForUpdate = true;

  @JsonIgnore
  private boolean isEmptyForUpdateToNotNull = true;

  @JsonIgnore
  public boolean isEmptyForUpdate() {
    return isEmptyForUpdate;
  }

  @JsonIgnore
  public void isEmptyForUpdate(boolean value) {
    isEmptyForUpdate = value;
  }

  @JsonIgnore
  public boolean isEmptyForUpdateToNotNull() {
    return isEmptyForUpdateToNotNull;
  }

  @JsonIgnore
  public void isEmptyForUpdateToNotNull(boolean value) {
    isEmptyForUpdateToNotNull = value;
  }

}
