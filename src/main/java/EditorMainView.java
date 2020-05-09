import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

class EditorMainView {
    private static final int INIT_WIDTH = 800;
    private static final int INIT_HEIGHT = 600;
    private final Stage stage;

    EditorMainView(Stage stage) {
        this.stage = stage;
    }

    void show() {
        StackPane root = new StackPane();

        CoordinatesBox coordinatesBox = new CoordinatesBox(root);

        root.getChildren().addAll(coordinatesBox);
        Scene scene = new Scene(root, INIT_WIDTH, INIT_HEIGHT);
        stage.setScene(scene);
        stage.show();
        showToolsWindow();
    }

    private void showToolsWindow() {
        ChildStage tools = new ChildStage(stage, "Tools");
        tools.show();
    }
}
