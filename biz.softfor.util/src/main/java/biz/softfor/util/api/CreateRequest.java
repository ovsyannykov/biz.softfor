package biz.softfor.util.api;

import biz.softfor.util.Create;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CreateRequest<K extends Number, D extends Identifiable<K>>
extends AbstractRequest {

  @NotNull(groups = { Create.class })
  @Valid
  public D data;
  
  public CreateRequest(D data) {
    this.data = data;
  }

  public CreateRequest() {
  }

}
