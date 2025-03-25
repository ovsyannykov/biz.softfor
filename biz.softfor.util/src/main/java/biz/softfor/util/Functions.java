package biz.softfor.util;

import java.util.Objects;

public interface Functions {

  @FunctionalInterface
  public static interface Four<T, U, V, W, R> {

    /**
     * Applies this function to the given arguments.
     *
     * @param t the first function argument
     * @param u the second function argument
     * @param v the third function argument
     * @param w the four function argument
     * @return the function result
     */
    R apply(T t, U u, V v, W w);

  }

  @FunctionalInterface
  public static interface FourConsumer<T, U, V, W> {

    public void accept(T t, U u, V v, W w);

    public default FourConsumer<T, U, V, W> andThen(FourConsumer<? super T, ? super U, ? super V, ? super W> after) {
      Objects.requireNonNull(after);
      return (t, u, v, w) -> {
        accept(t, u, v, w);
        after.accept(t, u, v, w);
      };
    }

  }

  @FunctionalInterface
  public static interface FiveConsumer<T, U, V, W, X> {

    public void accept(T t, U u, V v, W w, X x);

    public default FiveConsumer<T, U, V, W, X> andThen(FiveConsumer
    <? super T, ? super U, ? super V, ? super W, ? super X> after) {
      Objects.requireNonNull(after);
      return (t, u, v, w, x) -> {
        accept(t, u, v, w, x);
        after.accept(t, u, v, w, x);
      };
    }

  }

}
