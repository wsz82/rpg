package editor.view.asset;

import editor.model.EditorController;
import editor.view.asset.cover.CoverTableView;
import editor.view.asset.creature.CreatureTableView;
import editor.view.asset.equipment.container.ContainerTableView;
import editor.view.asset.equipment.countable.misc.MiscTableView;
import editor.view.asset.equipment.countable.weapon.WeaponsTableView;
import editor.view.asset.indoor.InDoorTableView;
import editor.view.asset.landscape.LandscapeTableView;
import editor.view.asset.lists.ObservableItemsList;
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
    }

    public void initWindow() {
        Scene scene = new Scene(root);
        setTitle(ASSETS);
        setScene(scene);
        createTabs();
    }

    private void createTabs() {
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        ObservableItemsList observableItemsList = editorController.getObservableAssets();

        ObservableList<Landscape> landscapes = observableItemsList.getLandscapes();
        AssetsTableView<Landscape> landscapeTableView = new LandscapeTableView(this, landscapes, editorCanvas, editorController);
        setUpTab(landscapeTableView, Paths.LANDSCAPES);

        ObservableList<Cover> covers = observableItemsList.getCovers();
        AssetsTableView<Cover> coverTableView = new CoverTableView(this, covers, editorCanvas, editorController);
        setUpTab(coverTableView, Paths.COVERS);

        ObservableList<Creature> creatures = observableItemsList.getCreatures();
        AssetsTableView<Creature> creatureTableView = new CreatureTableView(this, creatures, editorCanvas, editorController);
        setUpTab(creatureTableView, Paths.CREATURES);

        ObservableList<Weapon> weapons = observableItemsList.getWeapons();
        AssetsTableView<Weapon> weaponsTableView = new WeaponsTableView(this, weapons, editorCanvas, editorController);
        setUpTab(weaponsTableView, Paths.WEAPONS);

        ObservableList<Container> containers = observableItemsList.getContainers();
        AssetsTableView<Container> containerTableView = new ContainerTableView(this, containers, editorCanvas, editorController);
        setUpTab(containerTableView, Paths.CONTAINERS);

        ObservableList<Misc> miscs = observableItemsList.getMiscs();
        AssetsTableView<Misc> miscsTableView = new MiscTableView(this, miscs, editorCanvas, editorController);
        setUpTab(miscsTableView, Paths.MISC);

        ObservableList<Teleport> teleports = observableItemsList.getTeleports();
        AssetsTableView<Teleport> teleportTableView = new TeleportTableView(this, teleports, editorCanvas, editorController);
        setUpTab(teleportTableView, Paths.TELEPORTS);

        ObservableList<OutDoor> outDoors = observableItemsList.getOutDoors();
        AssetsTableView<OutDoor> outDoorTableView = new OutDoorTableView(this, outDoors, editorCanvas, editorController);
        setUpTab(outDoorTableView, Paths.OUTDOORS);

        ObservableList<InDoor> inDoors = observableItemsList.getInDoors();
        AssetsTableView<InDoor> inDoorTableView = new InDoorTableView(this, inDoors, editorCanvas, editorController);
        setUpTab(inDoorTableView, Paths.INDOORS);

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
