package view.stage;

import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.Controller;
import model.plugin.ActivePlugin;
import model.plugin.Plugin;
import view.asset.AssetsStage;
import view.content.ContentStage;
import view.layer.LayersStage;
import view.location.LocationParametersStage;
import view.location.LocationsStage;

public class MainView {
    private static final double INIT_WIDTH = 800;
    private static final double INIT_HEIGHT = 600;
    private static final Board BOARD = Board.get();
    private final ScrollPane scrollPane = new ScrollPane();
    private final Stage stage;
    private final ContentStage contentsWindow;
    private final LayersStage layersWindow;
    private final AssetsStage assetsWindow;
    private final LocationsStage locationsWindow;
    private double screenHeight;
    private double screenWidth;

    MainView(Stage stage) {
        this.stage = stage;
        contentsWindow = new ContentStage(stage);
        layersWindow = new LayersStage(stage);
        assetsWindow = new AssetsStage(stage);
        locationsWindow = new LocationsStage(stage);
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
        createLocationsWindow();
    }

    private void getScreenDimensions() {
        Rectangle2D screenBound = Screen.getPrimary().getBounds();
        screenHeight = screenBound.getMaxY();
        screenWidth = screenBound.getMaxX();
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
        CurrentLocationBox currentLocationBox = new CurrentLocationBox();
        CurrentLayerBox currentLayerBox = new CurrentLayerBox();
        bottomHorizontalBar.getChildren().addAll(coordinatesBox, currentLocationBox, currentLayerBox);
        bottom.getChildren().addAll(bottomHorizontalBar);
    }

    private MenuBar getMenuBar() {
        Menu file = new Menu("File");
        MenuItem newPlugin = new MenuItem("New");
        MenuItem save = new MenuItem("Save");
        MenuItem saveAs = new MenuItem("Save as");
        MenuItem load = new MenuItem("Load");

        newPlugin.setOnAction(event -> createNewPlugin());
        save.setOnAction(event -> saveFile());
        saveAs.setOnAction(event -> saveAsFile());
        load.setOnAction(event -> loadFile());
        file.getItems().addAll(newPlugin, save, saveAs, load);

        Menu view = new Menu("View");
        CheckMenuItem contents = new CheckMenuItem("Contents");
        CheckMenuItem layers = new CheckMenuItem("Layers");
        CheckMenuItem assets = new CheckMenuItem("Assets");
        CheckMenuItem locations = new CheckMenuItem("Locations");

        setViewItemOnAction(contentsWindow, contents);
        setViewItemOnAction(layersWindow, layers);
        setViewItemOnAction(assetsWindow, assets);
        setViewItemOnAction(locationsWindow, locations);
        view.getItems().addAll(contents, layers, assets, locations);

        Menu location = new Menu("Location");
        MenuItem parameters = new MenuItem("Parameters");
        parameters.setOnAction(event -> {
            LocationParametersStage locationParametersStage = new LocationParametersStage(stage);
            locationParametersStage.show();
        });
        location.getItems().addAll(parameters);

        return new MenuBar(file, view, location);
    }

    private void createNewPlugin() {
        Controller.get().initNewPlugin();
    }

    private void loadFile() {
        Plugin plugin = new Plugin();
        plugin.load();
    }

    private void saveAsFile() {
        Plugin plugin = new Plugin();
        plugin.saveAs();
    }

    private void saveFile() {
        Plugin plugin = new Plugin();
        if (ActivePlugin.get().getActivePlugin() != null) {
            plugin = ActivePlugin.get().getActivePlugin();
            plugin.save();
        } else {
            plugin.saveAs();
        }
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

    private void createLocationsWindow() {
        locationsWindow.setX((double) 15/20 * screenWidth);
        locationsWindow.setY(0);
        locationsWindow.show();
    }

    private void showOrHide(Stage stage, boolean isSelected) {
        if (!isSelected) {
            stage.hide();
        } else {
            stage.show();
        }
    }
}
