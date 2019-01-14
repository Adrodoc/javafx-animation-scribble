package scribble;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.animation.PathTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.VLineTo;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Scribble extends Application {
  private TextArea textArea;
  private StackPane root;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) throws Exception {
    root = FXMLLoader.load(getClass().getResource("/application.fxml"));
    textArea = (TextArea) root.getChildren().iterator().next();

    Scene scene = new Scene(root);

    scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
    stage.setScene(scene);
    stage.show();

    ExecutorService executor = Executors.newSingleThreadExecutor();
    executor.submit(() -> {
      for (int i = 0; i < 5; i++) {
        Platform.runLater(() -> {
          createSquare();
        });
        Thread.sleep(5000);
      }
      return null;
    });
  }

  private void createSquare() {
    double halfWidth = 10;
    double centerX = halfWidth;
    double centerY = halfWidth;

    Polygon square = new Polygon(//
        centerX - halfWidth, centerY - halfWidth, //
        centerX - halfWidth, centerY + halfWidth, //
        centerX + halfWidth, centerY + halfWidth, //
        centerX + halfWidth, centerY - halfWidth //
    );
    square.setFocusTraversable(true);
    square.setOnMouseClicked(e -> square.requestFocus());
    square.focusedProperty().addListener(new ChangeListener<Boolean>() {
      @Override
      public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
          Boolean newValue) {
        if (newValue) {
          textArea.setText(String.valueOf(square.hashCode()));
        }
      }
    });
    square.fillProperty().bind(Bindings.createObjectBinding(
        () -> square.isFocused() ? Color.BLUE : Color.BLACK, square.focusedProperty()));

    Path path = new Path(//
        new MoveTo(578, 268), //
        new HLineTo(720), //
        new VLineTo(118), //
        new HLineTo(886), //
        new VLineTo(558), //
        new HLineTo(164), //
        new VLineTo(352), //
        new HLineTo(538) //
    );
    new PathTransition(Duration.seconds(60), path, square).play();

    root.getChildren().add(square);
  }
}
