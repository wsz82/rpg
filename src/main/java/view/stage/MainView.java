package view.stage;

import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import view.assets.AssetsStage;
import view.content.ContentStage;
import view.layers.LayersStage;

class MainView {
    private static final String CONTENTS = "Contents";
    private static final String LAYERS = "Layers";
    private static final String ASSETS = "Assets";
    private static final int INIT_WIDTH = 800;
    private static final int INIT_HEIGHT = 600;
    private final Stage stage;
    private ContentStage contentsWindow;
    private LayersStage layersWindow;
    private AssetsStage assetsWindow;
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

        getScreenDimensions();
        createContentsWindow();
        createLayersWindow();
        createAssetsWindow();
    }

    private void getScreenDimensions() {
        Rectangle2D screenBound = Screen.getPrimary().getBounds();
        screenHeight = (int) screenBound.getMaxY();
        screenWidth = (int) screenBound.getMaxX();
    }

    private void setTopContent(VBox upperBar) {
        MenuBar menuBar = getMenuBar();
        upperBar.getChildren().addAll(menuBar);
    }

    private void setBottomContent(ScrollPane center, VBox bottom) {
        HBox bottomHorizontalBar = new HBox();
        bottomHorizontalBar.setSpacing(10);
        CoordinatesBox coordinatesBox = new CoordinatesBox(center);
        CurrentLayerBox currentLayerBox = new CurrentLayerBox();
        bottomHorizontalBar.getChildren().addAll(coordinatesBox, currentLayerBox);
        bottom.getChildren().addAll(bottomHorizontalBar);
    }

    private MenuBar getMenuBar() {
        Menu view = new Menu("View");
        MenuItem contents = new MenuItem(CONTENTS);
        MenuItem layers = new MenuItem(LAYERS);
        MenuItem assets = new MenuItem(ASSETS);
        contents.setOnAction(event ->
                showOrHide(contentsWindow)
        );
        layers.setOnAction(event ->
                showOrHide(layersWindow)
        );
        assets.setOnAction(event ->
                showOrHide(assetsWindow)
        );
        view.getItems().addAll(contents, layers, assets);
        return new MenuBar(view);
    }

    private void createContentsWindow() {
        contentsWindow = new ContentStage(stage);
        contentsWindow.setX(0);
        contentsWindow.setY((double) 5/10 * screenHeight);
        contentsWindow.show();
    }

    private void createLayersWindow() {
        layersWindow = new LayersStage(stage);
        layersWindow.setX(0);
        layersWindow.setY(0);
        layersWindow.show();
    }

    private void createAssetsWindow() {
        assetsWindow = new AssetsStage(stage);
        assetsWindow.setX((double) 15/20 * screenWidth);
        assetsWindow.setY((double) 5/10 * screenHeight);
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
