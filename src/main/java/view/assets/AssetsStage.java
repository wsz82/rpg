package view.assets;

import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.items.ItemType;

public class AssetsStage extends Stage {
    private static final String ASSETS = "Assets";
    private final StackPane root = new StackPane();
    private final TabPane tabPane = new TabPane();
    private final Stage parent;

    public AssetsStage(Stage parent) {
        super(StageStyle.UTILITY);
        this.parent = parent;
        initWindow();
    }

    private void initWindow() {
        Scene scene = new Scene(root);
        this.initOwner(parent);
        this.setTitle(ASSETS);
        this.setAlwaysOnTop(true);
        this.setScene(scene);

        createTabs();
    }

    private void createTabs() {
        ItemType[] itemTypes = ItemType.values();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        for (ItemType itemType:
             itemTypes) {
            AssetsTableView table = new AssetsTableView(this, itemType);
            String tabName = itemType.toString();
            Tab tab = new Tab();
            tab.setText(tabName);
            tab.setContent(table);
            tabPane.getTabs().add(tab);
        }
        root.getChildren().add(tabPane);
    }
}
