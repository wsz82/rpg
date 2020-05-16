package view.content;

import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import view.stage.ChildStage;

public class ContentStage extends ChildStage {
    private static final String CONTENT = "Content";
    private final StackPane root = new StackPane();
    private final ContentTableView table = new ContentTableView();

    public ContentStage(Stage parent) {
        super(parent);
        initWindow();
    }

    private void initWindow() {
        Scene scene = new Scene(root);
        initOwner(parent);
        setTitle(CONTENT);
        setScene(scene);

        setUpContextMenu();

        root.getChildren().add(table);
    }

    private void setUpContextMenu() {
        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem removeItems = new MenuItem("Remove items");
        final MenuItem changeVisibility = new MenuItem("Change visibility");
        contextMenu.getItems().addAll(removeItems, changeVisibility);
        removeItems.setOnAction(event -> removeItems());
        changeVisibility.setOnAction(event -> changeVisibility());
        root.setOnContextMenuRequested(event -> {
            contextMenu.show(this, event.getScreenX(), event.getScreenY());
        });
    }

    private void changeVisibility() {
        table.changeVisibility();
    }

    private void removeItems() {
        table.removeContents();
    }
}
