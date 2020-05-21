package editor.view.stage;

import editor.view.asset.AssetsStage;
import editor.view.content.ContentStage;
import editor.view.layer.LayersStage;
import editor.view.location.LocationParametersStage;
import editor.view.location.LocationsStage;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.Controller;
import model.plugin.ActivePlugin;
import model.plugin.Plugin;

import java.io.File;

class MainView {
    private static final double INIT_WIDTH = 800;
    private static final double INIT_HEIGHT = 600;
    private static final AnchorPane EDITOR_BOARD = EditorBoard.get();
    private final ScrollPane scrollPane = new ScrollPane();
    private final Stage stage;
    private final ContentStage contentsWindow;
    private final LayersStage layersWindow;
    private final AssetsStage assetsWindow;
    private final LocationsStage locationsWindow;

    MainView(Stage stage) {
        this.stage = stage;
        contentsWindow = new ContentStage(stage);
        layersWindow = new LayersStage(stage);
        assetsWindow = new AssetsStage(stage);
        locationsWindow = new LocationsStage(stage);
    }

    void show(File programDir) {
        BorderPane borderPane = new BorderPane();
        VBox topBar = new VBox();
        VBox bottomBar = new VBox();

        scrollPane.setContent(EDITOR_BOARD);
        scrollPane.setPannable(true);
        scrollPane.addEventFilter(ScrollEvent.SCROLL, Event::consume);
        scrollPane.addEventFilter(MouseEvent.ANY, event -> {
            if (!event.getEventType().equals(MouseEvent.MOUSE_MOVED)
                    && !event.getEventType().equals(MouseEvent.MOUSE_CLICKED)
                    && event.getButton() != MouseButton.MIDDLE) event.consume();
        });

        borderPane.setTop(topBar);
        borderPane.setCenter(scrollPane);
        borderPane.setBottom(bottomBar);

        setTopContent(topBar);
        setBottomContent(bottomBar);

        Scene scene = new Scene(borderPane, INIT_WIDTH, INIT_HEIGHT);
        stage.setScene(scene);

        restoreSettings(programDir);
        stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, event -> {
            storeSettings(programDir);
        });

        stage.show();
        layersWindow.show();
        assetsWindow.show();
        contentsWindow.show();
        locationsWindow.show();
    }

    private void storeSettings(File programDir) {
        SettingsMemento memento = new SettingsMemento(
                stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight(),
                layersWindow.getX(), layersWindow.getY(), layersWindow.getWidth(), layersWindow.getHeight(),
                assetsWindow.getX(), assetsWindow.getY(), assetsWindow.getWidth(), assetsWindow.getHeight(),
                contentsWindow.getX(), contentsWindow.getY(), contentsWindow.getWidth(), contentsWindow.getHeight(),
                locationsWindow.getX(), locationsWindow.getY(), locationsWindow.getWidth(), locationsWindow.getHeight()
        );
        memento.saveMemento(programDir);
    }

    private void restoreSettings(File programDir) {
        SettingsMemento memento = new SettingsMemento();
        memento = memento.loadMemento(programDir);
        stage.setX(memento.getStageX());
        stage.setY(memento.getStageY());
        stage.setWidth(memento.getStageWidth());
        stage.setHeight(memento.getStageHeight());
        layersWindow.setX(memento.getLayersX());
        layersWindow.setY(memento.getLayersY());
        layersWindow.setWidth(memento.getLayersWidth());
        layersWindow.setHeight(memento.getLayersHeight());
        assetsWindow.setX(memento.getAssetsX());
        assetsWindow.setY(memento.getAssetsY());
        assetsWindow.setWidth(memento.getAssetsWidth());
        assetsWindow.setHeight(memento.getAssetsHeight());
        contentsWindow.setX(memento.getContentsX());
        contentsWindow.setY(memento.getContentsY());
        contentsWindow.setWidth(memento.getContentsWidth());
        contentsWindow.setHeight(memento.getContentsHeight());
        locationsWindow.setX(memento.getLocationsX());
        locationsWindow.setY(memento.getLocationsY());
        locationsWindow.setWidth(memento.getLocationsWidth());
        locationsWindow.setHeight(memento.getLocationsHeight());
    }

    private void setTopContent(VBox topBar) {
        MenuBar menuBar = getMenuBar();
        ToolBar toolBar = new EditorToolBar(stage);
        topBar.getChildren().addAll(menuBar, toolBar);
    }

    private void setBottomContent(VBox bottom) {
        HBox bottomHorizontalBar = new HBox();
        bottomHorizontalBar.setSpacing(10);
        CoordinatesBox coordinatesBox = new CoordinatesBox(EDITOR_BOARD);
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

    private void showOrHide(Stage stage, boolean isSelected) {
        if (!isSelected) {
            stage.hide();
        } else {
            stage.show();
        }
    }
}
