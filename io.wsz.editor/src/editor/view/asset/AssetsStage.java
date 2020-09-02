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
import io.wsz.model.sizes.Paths;
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

        ObservableAssets observableAssets = editorController.getObservableAssets();

        ObservableList<Creature> creatures = observableAssets.getCreatures();
        AssetsTableView<Creature> creatureTableView = new CreatureTableView(this, creatures, editorCanvas, editorController);
        setUpTab(creatureTableView, Paths.CREATURES);

        ObservableList<Cover> covers = observableAssets.getCovers();
        AssetsTableView<Cover> coverTableView = new CoverTableView(this, covers, editorCanvas, editorController);
        setUpTab(coverTableView, Paths.COVERS);

        ObservableList<Landscape> landscapes = observableAssets.getLandscapes();
        AssetsTableView<Landscape> landscapeTableView = new LandscapeTableView(this, landscapes, editorCanvas, editorController);
        setUpTab(landscapeTableView, Paths.LANDSCAPES);

        ObservableList<Teleport> teleports = observableAssets.getTeleports();
        AssetsTableView<Teleport> teleportTableView = new TeleportTableView(this, teleports, editorCanvas, editorController);
        setUpTab(teleportTableView, Paths.TELEPORTS);

        ObservableList<Weapon> weapons = observableAssets.getWeapons();
        AssetsTableView<Weapon> weaponsTableView = new WeaponsTableView(this, weapons, editorCanvas, editorController);
        setUpTab(weaponsTableView, Paths.WEAPONS);

        ObservableList<Container> containers = observableAssets.getContainers();
        AssetsTableView<Container> containerTableView = new ContainerTableView(this, containers, editorCanvas, editorController);
        setUpTab(containerTableView, Paths.CONTAINERS);

        ObservableList<InDoor> inDoors = observableAssets.getInDoors();
        AssetsTableView<InDoor> inDoorTableView = new InDoorTableView(this, inDoors, editorCanvas, editorController);
        setUpTab(inDoorTableView, Paths.INDOORS);

        ObservableList<OutDoor> outDoors = observableAssets.getOutDoors();
        AssetsTableView<OutDoor> outDoorTableView = new OutDoorTableView(this, outDoors, editorCanvas, editorController);
        setUpTab(outDoorTableView, Paths.OUTDOORS);

        root.getChildren().add(tabPane);
    }

    private <A extends PosItem<?,?>> void setUpTab(AssetsTableView<A> tableView, String tabName) {
        tableView.setPointer(pointer);
        tableView.setContentTableView(contentTableView);
        Tab tab = new Tab();
        tab.setText(tabName);
        tab.setContent(tableView);
        tabPane.getTabs().add(tab);
    }
}
