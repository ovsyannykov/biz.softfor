package biz.softfor.util.api;

import lombok.Data;

@Data
public class Order {

  public static interface Direction {

    public final static String ASC = "ASC";
    public final static String DESC = "DESC";

  }

  private String property;
  private String direction = Direction.ASC;

  public Order(String direction, String property) {
    this.property = property;
    this.direction = direction;
  }

  public Order() {
    this("", "");
  }

}
