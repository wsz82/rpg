import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

class EditorMainView {
    private static final int INIT_WIDTH = 800;
    private static final int INIT_HEIGHT = 600;
    private final Stage stage;
    private ChildStage toolsWindow;

    EditorMainView(Stage stage) {
        this.stage = stage;
    }

    void show() {
        BorderPane borderPane = new BorderPane();
        VBox upperBar = new VBox();
        StackPane center = new StackPane();
        VBox downBar = new VBox();

        borderPane.setTop(upperBar);
        borderPane.setCenter(center);
        borderPane.setBottom(downBar);

        setTopContent(upperBar);
        setBottomContent(center, downBar);

        Scene scene = new Scene(borderPane, INIT_WIDTH, INIT_HEIGHT);
        stage.setScene(scene);
        stage.show();
        showToolsWindow();
    }

    private void setTopContent(VBox upperBar) {
        MenuBar menuBar = getMenuBar();
        upperBar.getChildren().addAll(menuBar);
    }

    private void setBottomContent(StackPane center, VBox downBar) {
        CoordinatesBox coordinatesBox = new CoordinatesBox(center);
        downBar.getChildren().addAll(coordinatesBox);
    }

    private MenuBar getMenuBar() {
        Menu view = new Menu("View");
        MenuItem tools = new MenuItem("Tools");
        tools.setOnAction(this::showOrHide);
        view.getItems().add(tools);
        return new MenuBar(view);
    }

    private void showToolsWindow() {
        toolsWindow = new ChildStage(stage, "Tools");
        toolsWindow.setX(INIT_WIDTH / 10);
        toolsWindow.setY(INIT_HEIGHT / 10);
        toolsWindow.show();
    }

    private void showOrHide(ActionEvent event) {
        if (toolsWindow.isShowing()) {
            toolsWindow.hide();
        } else {
            toolsWindow.show();
        }
    }
}
