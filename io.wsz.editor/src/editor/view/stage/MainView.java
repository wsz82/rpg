package editor.view.stage;

import editor.model.EditorController;
import editor.model.settings.SettingsMemento;
import editor.view.asset.AssetsStage;
import editor.view.content.ContentStage;
import editor.view.content.ContentTableView;
import editor.view.layer.LayersStage;
import editor.view.location.LocationParametersStage;
import editor.view.location.LocationsStage;
import editor.view.plugin.EditorPluginsTable;
import editor.view.plugin.PluginSettingsStage;
import io.wsz.model.plugin.ActivePlugin;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

class MainView {
    private static final double INIT_WIDTH = 800;
    private static final double INIT_HEIGHT = 600;
    private final EditorCanvas editorCanvas;
    private final Stage stage;
    private final Pane center;
    private final ContentStage contentsWindow;
    private final LayersStage layersWindow;
    private final AssetsStage assetsWindow;
    private final LocationsStage locationsWindow;
    private final PluginSettingsStage pss;
    private final Pointer pointer;
    private final EditorController editorController = EditorController.get();

    MainView(Stage stage) {
        this.stage = stage;
        this.center = new Pane();
        pointer = new Pointer();
        editorCanvas = new EditorCanvas(stage, center, pointer);
        center.getChildren().add(editorCanvas);
        contentsWindow = new ContentStage(stage, editorCanvas);
        final ContentTableView ctv = contentsWindow.getTable();
        editorCanvas.setContentTableView(ctv);
        layersWindow = new LayersStage(stage, ctv, editorCanvas);
        assetsWindow = new AssetsStage(stage, pointer, ctv);
        locationsWindow = new LocationsStage(stage);
        pss = new PluginSettingsStage(stage);
    }

    void show() {
        final BorderPane borderPane = new BorderPane();
        final VBox topBar = new VBox();
        final VBox bottomBar = new VBox();

        borderPane.setTop(topBar);
        borderPane.setCenter(center);
        borderPane.setBottom(bottomBar);

        setTopContent(topBar);
        setBottomContent(bottomBar);

        final Scene scene = new Scene(borderPane, INIT_WIDTH, INIT_HEIGHT);
        stage.setScene(scene);

        restoreSettings(Main.getDir());

        stage.show();
        layersWindow.show();
        assetsWindow.show();
        contentsWindow.show();
        locationsWindow.show();

        hookUpEvents();
    }

    private void hookUpEvents() {
        stage.setOnCloseRequest(event -> {
            onCloseRequest();
        });
    }

    private void onCloseRequest() {
        askForSave();
        storeSettings(Main.getDir());
    }

    private void askForSave() {
        final Alert alert = new Alert(
                Alert.AlertType.CONFIRMATION, "Would you like to save?", ButtonType.NO, ButtonType.YES);
        alert.showAndWait()
                .filter(r -> r == ButtonType.YES)
                .ifPresent(r -> {
                    saveFile();
                    alert.close();
                });
    }

    private void storeSettings(File programDir) {
        SettingsMemento memento = new SettingsMemento(
                stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight(),
                layersWindow.getX(), layersWindow.getY(), layersWindow.getWidth(), layersWindow.getHeight(),
                assetsWindow.getX(), assetsWindow.getY(), assetsWindow.getWidth(), assetsWindow.getHeight(),
                contentsWindow.getX(), contentsWindow.getY(), contentsWindow.getWidth(), contentsWindow.getHeight(),
                locationsWindow.getX(), locationsWindow.getY(), locationsWindow.getWidth(), locationsWindow.getHeight()
        );
        editorController.storeSettings(programDir, memento);
    }

    private void restoreSettings(File programDir) {
        SettingsMemento memento = editorController.restoreSettings(programDir);
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
        final MenuBar menuBar = getMenuBar();
        final ToolBar toolBar = getToolBar();
        topBar.getChildren().addAll(menuBar, toolBar);
    }

    private ToolBar getToolBar() {
        pointer.setEditorCanvas(editorCanvas);
        contentsWindow.setPointer(pointer);
        return new EditorToolBar(pointer);
    }

    private void setBottomContent(VBox bottom) {
        final HBox bottomHorizontalBar = new HBox();
        bottomHorizontalBar.setSpacing(10);
        final CoordinatesBox coordinatesBox = new CoordinatesBox(center);
        final CurrentLocationBox currentLocationBox = new CurrentLocationBox();
        final CurrentLayerBox currentLayerBox = new CurrentLayerBox();
        bottomHorizontalBar.getChildren().addAll(coordinatesBox, currentLocationBox, currentLayerBox);
        bottom.getChildren().addAll(bottomHorizontalBar);
    }

    private MenuBar getMenuBar() {
        final Menu file = new Menu("File");
        final MenuItem newPlugin = new MenuItem("New");
        final MenuItem save = new MenuItem("Save");
        final MenuItem saveAs = new MenuItem("Save as");
        final MenuItem plugins = new MenuItem("Plugins");
        final MenuItem plugin = new MenuItem("Plugin settings");
        final MenuItem exit = new MenuItem("Exit");

        newPlugin.setOnAction(event -> createNewPlugin());
        save.setOnAction(event -> saveFile());
        saveAs.setOnAction(event -> saveAsFile());
        plugins.setOnAction(event -> openPluginsTable());
        plugin.setOnAction(e -> openPluginSettings());
        exit.setOnAction(e -> {
            onCloseRequest();
            stage.close();
        });
        file.getItems().addAll(newPlugin, save, saveAs, plugins, plugin, exit);

        final Menu view = new Menu("View");
        final CheckMenuItem contents = new CheckMenuItem("Contents");
        final CheckMenuItem layers = new CheckMenuItem("Layers");
        final CheckMenuItem assets = new CheckMenuItem("Assets");
        final CheckMenuItem locations = new CheckMenuItem("Locations");

        setViewItemOnAction(contentsWindow, contents);
        setViewItemOnAction(layersWindow, layers);
        setViewItemOnAction(assetsWindow, assets);
        setViewItemOnAction(locationsWindow, locations);
        view.getItems().addAll(contents, layers, assets, locations);

        final Menu location = new Menu("Location");
        final MenuItem parameters = new MenuItem("Parameters");
        parameters.setOnAction(event -> {
            LocationParametersStage locationParametersStage = new LocationParametersStage(stage);
            locationParametersStage.show();
        });
        location.getItems().addAll(parameters);

        return new MenuBar(file, view, location);
    }

    private void openPluginSettings() {
        pss.open();
    }

    private void createNewPlugin() {
        onCloseRequest();
        editorController.initNewPlugin();
    }

    private void openPluginsTable() {
        final Stage plugins = new EditorPluginsTable(pss);
        plugins.initOwner(stage);
        plugins.show();
    }

    private void saveAsFile() {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save plugin");
        fileChooser.setInitialDirectory(Main.getDir());
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Plugin file", "*.rpg")
        );
        final File saveFile = fileChooser.showSaveDialog(stage);
        if (saveFile == null) {
            return;
        }
        editorController.savePluginAs(saveFile.getName(), pss);    //TODO fill only name
    }

    private void saveFile() {
        if (ActivePlugin.get().getPlugin() != null) {
            editorController.saveActivePlugin(pss);
        } else {
            saveAsFile();
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
