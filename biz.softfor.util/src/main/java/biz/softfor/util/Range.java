package biz.softfor.util;

public class Range<T extends Comparable<? super T>> {

  private T from;
  private T to;

  public Range() {
  }

  public Range(T from, T to) {
    this.from = from;
    this.to = to;
  }

  public T getFrom() {
    return from;
  }

  public void setFrom(T from) {
    this.from = from;
  }

  public T getTo() {
    return to;
  }

  public void setTo(T to) {
    this.to = to;
  }

}
