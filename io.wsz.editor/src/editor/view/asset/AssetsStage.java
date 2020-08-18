package editor.view.asset;

import editor.model.EditorController;
import editor.view.asset.cover.CoverTableView;
import editor.view.asset.creature.CreatureTableView;
import editor.view.asset.equipment.container.ContainerTableView;
import editor.view.asset.equipment.weapon.WeaponsTableView;
import editor.view.asset.indoor.InDoorTableView;
import editor.view.asset.landscape.LandscapeTableView;
import editor.view.asset.outdoor.OutDoorTableView;
import editor.view.asset.teleport.TeleportTableView;
import editor.view.content.ContentTableView;
import editor.view.stage.ChildStage;
import editor.view.stage.EditorCanvas;
import editor.view.stage.Pointer;
import io.wsz.model.item.*;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class AssetsStage extends ChildStage {
    private static final String ASSETS = "Assets";

    private final StackPane root = new StackPane();
    private final TabPane tabPane = new TabPane();
    private final Pointer pointer;
    private final ContentTableView contentTableView;
    private final EditorCanvas editorCanvas;
    private final EditorController editorController;

    public AssetsStage(Stage parent, Pointer pointer, ContentTableView contentTableView,
                       EditorCanvas editorCanvas, EditorController editorController) {
        super(parent);
        this.pointer = pointer;
        this.contentTableView = contentTableView;
        this.editorCanvas = editorCanvas;
        this.editorController = editorController;
        initWindow();
    }

    private void initWindow() {
        Scene scene = new Scene(root);
        setTitle(ASSETS);
        setScene(scene);

        createTabs();
    }

    private void createTabs() {
        ItemType[] itemTypes = ItemType.values();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        for (ItemType type:
             itemTypes) {
            AssetsTableView table = null;
            switch (type) {
                case CREATURE -> {
                    ObservableList<Creature> creatures = editorController.getObservableAssets().getCreatures();
                    table = new CreatureTableView(this, creatures, editorCanvas, editorController);
                }
                case COVER -> {
                    ObservableList<Cover> covers = editorController.getObservableAssets().getCovers();
                    table = new CoverTableView(this, covers, editorCanvas, editorController);
                }
                case LANDSCAPE -> {
                    ObservableList<Landscape> landscapes = editorController.getObservableAssets().getLandscapes();
                    table = new LandscapeTableView(this, landscapes, editorCanvas, editorController);
                }
                case TELEPORT -> {
                    ObservableList<Teleport> teleports = editorController.getObservableAssets().getTeleports();
                    table = new TeleportTableView(this, teleports, editorCanvas, editorController);
                }
                case WEAPON -> {
                    ObservableList<Weapon> weapons = editorController.getObservableAssets().getWeapons();
                    table = new WeaponsTableView(this, weapons, editorCanvas, editorController);
                }
                case CONTAINER -> {
                    ObservableList<Container> containers = editorController.getObservableAssets().getContainers();
                    table = new ContainerTableView(this, containers, editorCanvas, editorController);
                }
                case INDOOR -> {
                    ObservableList<InDoor> inDoors = editorController.getObservableAssets().getInDoors();
                    table = new InDoorTableView(this, inDoors, editorCanvas, editorController);
                }
                case OUTDOOR -> {
                    ObservableList<OutDoor> outDoors = editorController.getObservableAssets().getOutDoors();
                    table = new OutDoorTableView(this, outDoors, editorCanvas, editorController);
                }
            }
            if (table == null) continue;
            table.setPointer(pointer);
            table.setContentTableView(contentTableView);
            String tabName = type.toString();
            Tab tab = new Tab();
            tab.setText(tabName);
            tab.setContent(table);
            tabPane.getTabs().add(tab);
        }
        root.getChildren().add(tabPane);
    }
}
