package view.stage;

import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import view.assets.AssetsStage;
import view.content.ContentStage;
import view.layers.LayersStage;
import view.locations.LocationStage;

public class MainView {
    private static final String CONTENTS = "Contents";
    private static final String LAYERS = "Layers";
    private static final String ASSETS = "Assets";
    private static final int INIT_WIDTH = 800;
    private static final int INIT_HEIGHT = 600;
    private static final Board BOARD = new Board();
    private final ScrollPane scrollPane = new ScrollPane();
    private final Stage stage;
    private final ContentStage contentsWindow;
    private final LayersStage layersWindow;
    private final AssetsStage assetsWindow;
    private double screenHeight;
    private double screenWidth;

    MainView(Stage stage) {
        this.stage = stage;
        contentsWindow = new ContentStage(stage);
        layersWindow = new LayersStage(stage);
        assetsWindow = new AssetsStage(stage);
    }

    void show() {
        BorderPane borderPane = new BorderPane();
        VBox topBar = new VBox();
        VBox bottomBar = new VBox();

        scrollPane.setContent(BOARD);
        borderPane.setTop(topBar);
        borderPane.setCenter(scrollPane);
        borderPane.setBottom(bottomBar);

        setTopContent(topBar);
        setBottomContent(bottomBar);

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

    private void setTopContent(VBox topBar) {
        MenuBar menuBar = getMenuBar();
        ToolBar toolBar = new EditorToolBar(stage);
        topBar.getChildren().addAll(menuBar, toolBar);
    }

    private void setBottomContent(VBox bottom) {
        HBox bottomHorizontalBar = new HBox();
        bottomHorizontalBar.setSpacing(10);
        CoordinatesBox coordinatesBox = new CoordinatesBox(BOARD);
        CurrentLayerBox currentLayerBox = new CurrentLayerBox();
        bottomHorizontalBar.getChildren().addAll(coordinatesBox, currentLayerBox);
        bottom.getChildren().addAll(bottomHorizontalBar);
    }

    private MenuBar getMenuBar() {
        Menu view = new Menu("View");
        CheckMenuItem contents = new CheckMenuItem(CONTENTS);
        CheckMenuItem layers = new CheckMenuItem(LAYERS);
        CheckMenuItem assets = new CheckMenuItem(ASSETS);

        setViewItemOnAction(contentsWindow, contents);
        setViewItemOnAction(layersWindow, layers);
        setViewItemOnAction(assetsWindow, assets);
        view.getItems().addAll(contents, layers, assets);

        Menu location = new Menu("Location");
        MenuItem parameters = new MenuItem("Parameters");
        parameters.setOnAction(event -> {
            LocationStage locationStage = new LocationStage(stage);
            locationStage.show();
        });
        location.getItems().addAll(parameters);

        return new MenuBar(view, location);
    }

    private void setViewItemOnAction(Stage stage, CheckMenuItem menuItem) {
        menuItem.setSelected(true);
        stage.setOnCloseRequest(event -> menuItem.setSelected(false));
        menuItem.setOnAction(event ->
                showOrHide(stage, menuItem.isSelected())
        );
    }

    private void createContentsWindow() {
        contentsWindow.setX(0);
        contentsWindow.setY((double) 5/10 * screenHeight);
        contentsWindow.show();
    }

    private void createLayersWindow() {
        layersWindow.setX(0);
        layersWindow.setY(0);
        layersWindow.show();
    }

    private void createAssetsWindow() {
        assetsWindow.setX((double) 15/20 * screenWidth);
        assetsWindow.setY((double) 5/10 * screenHeight);
        assetsWindow.show();
    }

    private void showOrHide(Stage stage, boolean isSelected) {
        if (!isSelected) {
            stage.hide();
        } else {
            stage.show();
        }
    }

    public static Board getBoard() {
        return BOARD;
    }
}
