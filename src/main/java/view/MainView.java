package view;

import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

class MainView {
    private static final String TOOLS = "Tools";
    private static final String CONTENTS = "Contents";
    private static final String LAYERS = "Layers";
    private static final String ASSETS = "Assets";
    private static final int INIT_WIDTH = 800;
    private static final int INIT_HEIGHT = 600;
    private final Stage stage;
    private ChildStage toolsWindow;
    private ContentStage contentsWindow;
    private LayersStage layersWindow;
    private ChildStage assetsWindow;
    private double screenHeight;
    private double screenWidth;

    MainView(Stage stage) {
        this.stage = stage;
    }

    void show() {
        BorderPane borderPane = new BorderPane();
        VBox upperBar = new VBox();
        ScrollPane center = new ScrollPane();
        VBox downBar = new VBox();

        borderPane.setTop(upperBar);
        borderPane.setCenter(center);
        borderPane.setBottom(downBar);

        setTopContent(upperBar);
        setBottomContent(center, downBar);

        Scene scene = new Scene(borderPane, INIT_WIDTH, INIT_HEIGHT);
        stage.setScene(scene);
        stage.show();

        Rectangle2D screenBound = Screen.getPrimary().getBounds();
        screenHeight = (int) screenBound.getMaxY();
        screenWidth = (int) screenBound.getMaxX();
        createToolsWindow();
        createContentsWindow();
        createLayersWindow();
        createAssetsWindow();
    }

    private void setTopContent(VBox upperBar) {
        MenuBar menuBar = getMenuBar();
        upperBar.getChildren().addAll(menuBar);
    }

    private void setBottomContent(ScrollPane center, VBox downBar) {
        CoordinatesBox coordinatesBox = new CoordinatesBox(center);
        downBar.getChildren().addAll(coordinatesBox);
    }

    private MenuBar getMenuBar() {
        Menu view = new Menu("View");
        MenuItem tools = new MenuItem(TOOLS);
        MenuItem contents = new MenuItem(CONTENTS);
        MenuItem layers = new MenuItem(LAYERS);
        MenuItem assets = new MenuItem(ASSETS);
        tools.setOnAction(event ->
            showOrHide(toolsWindow)
            );
        contents.setOnAction(event ->
                showOrHide(contentsWindow)
        );
        layers.setOnAction(event ->
                showOrHide(layersWindow)
        );
        assets.setOnAction(event ->
                showOrHide(assetsWindow)
        );
        view.getItems().addAll(tools, contents, layers, assets);
        return new MenuBar(view);
    }

    private void createToolsWindow() {
        toolsWindow = new ChildStage(stage, "Tools");
        toolsWindow.setX(screenWidth / 10);
        toolsWindow.setY(screenHeight / 10);
        toolsWindow.show();
    }

    private void createContentsWindow() {
        contentsWindow = new ContentStage(stage);
        contentsWindow.setX(screenWidth / 10);
        contentsWindow.setY((double) 7/10 * screenHeight);
        contentsWindow.show();
    }

    private void createLayersWindow() {
        layersWindow = new LayersStage(stage);
        layersWindow.setX(screenWidth / 10);
        layersWindow.setY((double) 3/10 * screenHeight);
        layersWindow.show();
    }

    private void createAssetsWindow() {
        assetsWindow = new ChildStage(stage, "Assets");
        assetsWindow.setX((double) 8/10 * screenWidth);
        assetsWindow.setY((double) 7/10 * screenHeight);
        assetsWindow.show();
    }

    private void showOrHide(Stage stage) {
        if (stage.isShowing()) {
            stage.hide();
        } else {
            stage.show();
        }
    }
}
