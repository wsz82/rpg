package editor.view.stage;

import editor.model.EditorController;
import editor.model.settings.SettingsMemento;
import editor.view.asset.AssetsStage;
import editor.view.asset.equipment.weapon.type.WeaponTypeStage;
import editor.view.content.ContentStage;
import editor.view.content.ContentTableView;
import editor.view.layer.LayersStage;
import editor.view.location.LocationParametersStage;
import editor.view.location.LocationsStage;
import editor.view.plugin.EditorPluginsTable;
import editor.view.plugin.PluginSettingsStage;
import io.wsz.model.Controller;
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

    private final EditorController editorController;
    private final Controller controller;
    private final EditorCanvas editorCanvas;
    private final Stage stage;
    private final Pane center;
    private final ContentStage contentsWindow;
    private final LayersStage layersWindow;
    private final AssetsStage assetsWindow;
    private final LocationsStage locationsWindow;
    private final PluginSettingsStage pss;
    private final Pointer pointer;

    public MainView(Stage stage, EditorController editorController) {
        this.stage = stage;
        this.editorController = editorController;
        controller = editorController.getController();
        this.center = new Pane();
        pointer = new Pointer(controller);
        editorCanvas = new EditorCanvas(stage, editorController, center, pointer);
        center.getChildren().add(editorCanvas);
        contentsWindow = new ContentStage(stage, editorCanvas, editorController);
        final ContentTableView ctv = contentsWindow.getTable();
        editorCanvas.setContentTableView(ctv);
        layersWindow = new LayersStage(stage, ctv, editorCanvas, editorController);
        assetsWindow = new AssetsStage(stage, pointer, ctv, editorCanvas, editorController);
        locationsWindow = new LocationsStage(stage, editorController, editorCanvas);
        pss = new PluginSettingsStage(stage, editorController);
    }

    public void show() {
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

        File programDir = editorController.getController().getProgramDir();
        restoreSettings(programDir);

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
        File programDir = editorController.getController().getProgramDir();
        storeSettings(programDir);
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
        return new EditorToolBar(editorCanvas, pointer);
    }

    private void setBottomContent(VBox bottom) {
        final HBox bottomHorizontalBar = new HBox();
        bottomHorizontalBar.setSpacing(10);
        final CoordinatesBox coordinatesBox = new CoordinatesBox(center, controller);
        final CurrentLocationBox currentLocationBox = new CurrentLocationBox(controller);
        final CurrentLayerBox currentLayerBox = new CurrentLayerBox(controller);
        bottomHorizontalBar.getChildren().addAll(coordinatesBox, currentLocationBox, currentLayerBox);
        bottom.getChildren().addAll(bottomHorizontalBar);
    }

    private MenuBar getMenuBar() {
        final Menu file = getMenuFile();
        final Menu view = getMenuView();
        final Menu location = getMenuLocation();
        final Menu world = getMenuWorld();
        return new MenuBar(file, view, location, world);
    }

    private Menu getMenuWorld() {
        final Menu world = new Menu("World");
        final MenuItem weaponTypes = new MenuItem("Weapon types");

        weaponTypes.setOnAction(event -> {
            WeaponTypeStage weaponTypeStage = new WeaponTypeStage(stage, editorController);
            weaponTypeStage.show();
        });
        world.getItems().addAll(weaponTypes);
        return world;
    }

    private Menu getMenuLocation() {
        final Menu location = new Menu("Location");
        final MenuItem parameters = new MenuItem("Parameters");
        parameters.setOnAction(event -> {
            LocationParametersStage locationParametersStage = new LocationParametersStage(stage, controller);
            locationParametersStage.show();
        });
        location.getItems().addAll(parameters);
        return location;
    }

    private Menu getMenuView() {
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
        return view;
    }

    private Menu getMenuFile() {
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
        return file;
    }

    private void openPluginSettings() {
        pss.open();
    }

    private void createNewPlugin() {
        onCloseRequest();
        editorController.initNewPlugin();
    }

    private void openPluginsTable() {
        final Stage plugins = new EditorPluginsTable(pss, editorController);
        plugins.initOwner(stage);
        plugins.show();
    }

    private void saveAsFile() {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save plugin");
        File programDir = editorController.getController().getProgramDir();
        fileChooser.setInitialDirectory(programDir);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Plugin file", "*.rpg")
        );
        final File saveFile = fileChooser.showSaveDialog(stage);
        if (saveFile == null) {
            return;
        }
        editorController.savePluginAs(saveFile.getName(), pss);
    }

    private void saveFile() {
        if (editorController.getController().getModel().getActivePlugin() != null) {
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
