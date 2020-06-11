package editor.view.asset;

import editor.view.stage.ChildStage;
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

    public AssetsStage(Stage parent) {
        super(parent);
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

        for (ItemType itemType:
             itemTypes) {
            AssetsTableView table = null;
            switch (itemType) {
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
            }
            String tabName = itemType.toString();
            Tab tab = new Tab();
            tab.setText(tabName);
            tab.setContent(table);
            tabPane.getTabs().add(tab);
        }
        root.getChildren().add(tabPane);
    }
}
