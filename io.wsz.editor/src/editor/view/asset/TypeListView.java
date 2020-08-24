package editor.view.asset;

import editor.model.EditorController;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.StringConverter;

public abstract class TypeListView<A> extends ListView<A> {
    protected final EditorController editorController;
    protected final ContextMenu contextMenu = new ContextMenu();

    public TypeListView(EditorController editorController) {
        this.editorController = editorController;
        initList();
        setUpContextMenu();
        hookUpEvents();
    }

    private void initList() {
        setTypeItems();
        setEditable(true);
        StringConverter<A> stringConverter = getStringConverter();
        setCellFactory(param -> new TextFieldListCell<>(stringConverter));
    }

    protected abstract void setTypeItems();

    protected abstract StringConverter<A> getStringConverter();

    private void hookUpEvents() {
        setOnContextMenuRequested(e -> {
            contextMenu.show(this, e.getScreenX(), e.getScreenY());
        });
    }

    private void setUpContextMenu() {
        final MenuItem addType = new MenuItem("Add type");
        final MenuItem removeTypes = new MenuItem("Remove types");
        contextMenu.getItems().addAll(addType, removeTypes);
        addType.setOnAction(event -> addType());
        removeTypes.setOnAction(event -> removeTypes());
    }

    protected abstract void addType();

    protected abstract void removeTypes();
}
