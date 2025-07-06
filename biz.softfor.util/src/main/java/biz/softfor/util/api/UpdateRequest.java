package biz.softfor.util.api;

import biz.softfor.util.api.filter.FilterId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UpdateRequest
<K extends Number, F extends FilterId<K>, WOR extends Identifiable<K>>
extends DeleteRequest<K, F> {

  public WOR data;
  public List<String> fields;

  public UpdateRequest(WOR data, List<String> fields) {
    this.data = data;
    this.fields = fields;
  }

  public UpdateRequest(WOR data) {
    this(data, null);
  }

  public UpdateRequest() {
  }

  @AssertTrue(message = "Request 'data' or 'fields' must be not empty.")
  @JsonIgnore
  public boolean isValid() {
    return data != null || CollectionUtils.isNotEmpty(fields);
  }

}
