package editor.view.asset;

import editor.view.content.ContentTableView;
import editor.view.stage.ChildStage;
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

    public AssetsStage(Stage parent, Pointer pointer, ContentTableView contentTableView) {
        super(parent);
        this.pointer = pointer;
        this.contentTableView = contentTableView;
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
                    ObservableList<Creature> creatures = ObservableAssets.get().getCreatures();
                    table = new CreatureTableView(this, creatures);
                }
                case COVER -> {
                    ObservableList<Cover> covers = ObservableAssets.get().getCovers();
                    table = new CoverTableView(this, covers);
                }
                case LANDSCAPE -> {
                    ObservableList<Landscape> landscapes = ObservableAssets.get().getLandscapes();
                    table = new LandscapeTableView(this, landscapes);
                }
                case TELEPORT -> {
                    ObservableList<Teleport> teleports = ObservableAssets.get().getTeleports();
                    table = new TeleportTableView(this, teleports);
                }
                case WEAPON -> {
                    ObservableList<Weapon> weapons = ObservableAssets.get().getWeapons();
                    table = new WeaponsTableView(this, weapons);
                }
                case CONTAINER -> {
                    ObservableList<Container> containers = ObservableAssets.get().getContainers();
                    table = new ContainerTableView(this, containers);
                }
                case INDOOR -> {
                    ObservableList<InDoor> inDoors = ObservableAssets.get().getInDoors();
                    table = new InDoorTableView(this, inDoors);
                }
                case OUTDOOR -> {
                    ObservableList<OutDoor> outDoors = ObservableAssets.get().getOutDoors();
                    table = new OutDoorTableView(this, outDoors);
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
