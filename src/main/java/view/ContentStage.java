package view;

import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

class ContentStage extends Stage {
    private static final String CONTENT = "Content";
    private final StackPane root = new StackPane();
    private final ContextMenu contextMenu = new ContextMenu();
    private final MenuItem removeItems = new MenuItem("Remove items");
    private final Stage parent;
    private final ContentTableView table = new ContentTableView();

    ContentStage(Stage parent) {
        super(StageStyle.UTILITY);
        this.parent = parent;
        initWindow();
    }

    private void initWindow() {
        Scene scene = new Scene(root);
        this.initOwner(parent);
        this.setTitle(CONTENT);
        this.setAlwaysOnTop(true);
        this.setScene(scene);

        setUpContextMenu();

        root.getChildren().add(table);
    }

    private void setUpContextMenu() {
        fillContextMenu();

        root.setOnContextMenuRequested(event -> {
            contextMenu.show(this, event.getScreenX(), event.getScreenY());
        });
        removeItems.setOnAction(event -> removeItems());
    }

    private void fillContextMenu() {
        contextMenu.getItems().addAll(removeItems);
    }

    private void removeItems() {
        table.removeContents();
    }
}
