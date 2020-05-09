import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class EditorMain extends Application {
    private static final int INIT_WIDTH = 800;
    private static final int INIT_HEIGHT = 600;

    @Override
    public void start(Stage stage) {
        StackPane root = new StackPane();
        CoordinatesBox coordinatesBox = new CoordinatesBox(root);
        root.getChildren().addAll(coordinatesBox);
        Scene scene = new Scene(root, INIT_WIDTH, INIT_HEIGHT);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}