package editor.view.content;

import editor.view.stage.ChildStage;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ContentStage extends ChildStage {
    private static final String TITLE = "Content";
    private final StackPane root = new StackPane();
    private final ContentTableView table = ContentTableView.get();

    public ContentStage(Stage parent) {
        super(parent);
        initWindow();
    }

    private void initWindow() {
        Scene scene = new Scene(root);
        setTitle(TITLE);
        setScene(scene);

        setUpContextMenu();

        root.getChildren().add(table);
    }

    private void setUpContextMenu() {
        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem editContent = new MenuItem("Edit item");
        final MenuItem removeContents = new MenuItem("Remove items");
        final MenuItem moveToPointer = new MenuItem("Move to pointer");
        final MenuItem changeVisibility = new MenuItem("Change visibility");
        contextMenu.getItems().addAll(editContent, removeContents, moveToPointer, changeVisibility);
        editContent.setOnAction(event -> editContent());
        removeContents.setOnAction(event -> removeContents());
        moveToPointer.setOnAction(e -> moveToPointer());
        changeVisibility.setOnAction(event -> changeVisibility());
        root.setOnContextMenuRequested(event -> {
            contextMenu.show(this, event.getScreenX(), event.getScreenY());
        });
    }

    private void editContent() {
        table.editItem(this);
    }

    private void changeVisibility() {
        table.changeVisibility();
    }

    private void moveToPointer() {
        table.moveToPointer();
    }

    private void removeContents() {
        table.removeContents();
    }
}
