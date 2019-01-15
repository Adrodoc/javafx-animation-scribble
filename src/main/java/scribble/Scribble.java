package scribble;

import java.util.concurrent.Executors;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.VLineTo;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Scribble extends Application {
  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) throws Exception {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/application.fxml"));
    Region root = loader.load();
    Controller controller = loader.getController();
    initController(controller);
    stage.setScene(new Scene(root));
    stage.show();

    Executors.newSingleThreadExecutor().submit(() -> {
      for (int i = 0; i < 5; i++) {
        Thread.sleep(1000);
        Platform.runLater(() -> {
          controller.createBox();
        });
      }
      return null;
    });
  }

  private void initController(Controller controller) {
    Duration duration = Duration.seconds(60);
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
    Image image = new Image("Lagerplan2018_zoom2-1024x724.jpg");
    controller.setData(duration, path, image);
  }
}
