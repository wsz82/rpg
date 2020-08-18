package editor.view.asset.equipment.weapon.type;

import editor.model.EditorController;
import io.wsz.model.Controller;
import io.wsz.model.item.WeaponType;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.StringConverter;

public class WeaponTypeListView extends ListView<WeaponType> {
    private final EditorController editorController;
    private final Controller controller;
    private final ContextMenu contextMenu = new ContextMenu();

    public WeaponTypeListView(EditorController editorController) {
        this.editorController = editorController;
        controller = editorController.getController();
        initList();
        setUpContextMenu();
        hookUpEvents();
    }

    private void initList() {
        ObservableList<WeaponType> items = editorController.getObservableWeaponTypes();
        setItems(items);
        setEditable(true);
        setCellFactory(param -> new TextFieldListCell<>(new StringConverter<>() {
            @Override
            public String toString(WeaponType weaponType) {
                return weaponType.getName();
            }

            @Override
            public WeaponType fromString(String string) {
                return new WeaponType(string);
            }
        }));
    }

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

    private void addType() {
        WeaponType newType = new WeaponType("new");
        getItems().add(newType);
    }

    private void removeTypes() {
        ObservableList<WeaponType> selectedItems = getSelectionModel().getSelectedItems();
        getItems().removeAll(selectedItems);
    }
}
