package biz.softfor.util.api;

import biz.softfor.util.Create;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CreateRequest<K extends Number, WOR extends Identifiable<K>>
extends AbstractRequest {

  @NotNull(groups = { Create.class })
  @Valid
  public WOR data;
  
  public CreateRequest(WOR data) {
    this.data = data;
  }

  public CreateRequest() {
  }

}
