package view.assets;

import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.items.ItemType;
import view.stage.ChildStage;

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
        initOwner(parent);
        setTitle(ASSETS);
        setScene(scene);

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
