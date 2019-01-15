package scribble;

import java.util.function.Function;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ObservableValue;

public class Bindings2 {
  public static <T> DoubleBinding createDoubleBinding(ObservableValue<T> observable,
      Function<? super T, Double> function) {
    return Bindings.createDoubleBinding(() -> {
      T value = observable.getValue();
      return value == null ? 0 : function.apply(value);
    }, observable);
  }

  public static <T, R> ObjectBinding<R> createObjectBinding(ObservableValue<T> observable,
      Function<? super T, ? extends R> function) {
    return Bindings.createObjectBinding(() -> {
      T value = observable.getValue();
      return value == null ? null : function.apply(value);
    }, observable);
  }
}
