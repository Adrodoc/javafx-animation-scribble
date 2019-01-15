package scribble;

import static java.util.Objects.requireNonNull;
import static javafx.scene.layout.BackgroundPosition.CENTER;
import static javafx.scene.layout.BackgroundRepeat.NO_REPEAT;
import static scribble.Bindings2.createDoubleBinding;
import static scribble.Bindings2.createObjectBinding;
import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.util.Duration;

public class Controller {
  private static final BackgroundSize STRETCH = new BackgroundSize(1, 1, true, true, false, false);
  @FXML
  private TextArea textArea;
  @FXML
  private StackPane root;
  @FXML
  private StackPane animationPane;
  @FXML
  private Region background;

  private Duration duration;
  private Path path;
  private ObjectProperty<Image> image = new SimpleObjectProperty<>();

  public ObjectProperty<Image> imageProperty() {
    return image;
  }

  public Image getImage() {
    return imageProperty().get();
  }

  public void setImage(Image image) {
    imageProperty().set(image);
  }

  @FXML
  public void initialize() {
    background.backgroundProperty().bind(createObjectBinding(image,
        it -> new Background(new BackgroundImage(it, NO_REPEAT, NO_REPEAT, CENTER, STRETCH))));

    root.prefHeightProperty().bind(createDoubleBinding(background.backgroundProperty(), //
        background -> background.getImages().stream()//
            .map(it -> it.getImage())//
            .mapToDouble(it -> it.getHeight())//
            .max().orElse(0)//
    ));
    root.prefWidthProperty().bind(createDoubleBinding(background.backgroundProperty(), //
        background -> background.getImages().stream()//
            .map(it -> it.getImage())//
            .mapToDouble(it -> it.getWidth())//
            .max().orElse(0)//
    ));

    background.prefWidthProperty().bind(animationPane.widthProperty());
    background.prefHeightProperty().bind(animationPane.heightProperty());

    DoubleBinding width = createDoubleBinding(image, it -> it.getWidth());
    DoubleBinding height = createDoubleBinding(image, it -> it.getHeight());

    // Scale Animation
    animationPane.scaleXProperty().bind(animationPane.widthProperty().divide(width));
    animationPane.scaleYProperty().bind(animationPane.heightProperty().divide(height));

    // Ensure aspect ratio
    animationPane.prefWidthProperty().bind(Bindings.min(//
        root.widthProperty(), //
        root.heightProperty().multiply(width).divide(height)//
    ));
    animationPane.prefHeightProperty().bind(Bindings.min(//
        root.heightProperty(), //
        root.widthProperty().multiply(height).divide(width)//
    ));
  }

  public void setData(Duration duration, Path path, Image image) {
    this.duration = requireNonNull(duration, "duration == null!");
    this.path = requireNonNull(path, "path == null!");
    setImage(image);
  }

  public Node createSquare() {
    double halfWidth = 10;
    double centerX = getImage().getWidth() / 2;
    double centerY = getImage().getHeight() / 2;

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
    square.fillProperty()
        .bind(createObjectBinding(square.focusedProperty(), it -> it ? Color.BLUE : Color.BLACK));

    PathTransition transition = new PathTransition(duration, path, square);
    transition.setInterpolator(Interpolator.LINEAR);
    transition.setOnFinished(e -> System.exit(0));
    transition.play();

    animationPane.getChildren().add(square);
    return square;
  }

}
