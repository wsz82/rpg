package editor.view.asset.creature.inventory.place;

import editor.model.EditorController;
import editor.view.asset.TypeListView;
import io.wsz.model.item.InventoryPlaceType;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;

public class InventoryPlaceTypeListView extends TypeListView<InventoryPlaceType> {

    public InventoryPlaceTypeListView(EditorController editorController) {
        super(editorController);
        setOnEditCommit(e -> {
            String name = e.getNewValue().getName();
            InventoryPlaceType type = getSelectionModel().getSelectedItem();
            String oldName = type.getName();

            editorController.updateCreaturesInventoryPlacesNames(oldName, name);

            type.setName(name);
        });
    }

    @Override
    protected void setTypeItems() {
        ObservableList<InventoryPlaceType> items = editorController.getObservableInventoryPlacesTypes();
        setItems(items);
    }

    @Override
    protected StringConverter<InventoryPlaceType> getStringConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(InventoryPlaceType type) {
                if (type == null) {
                    return "";
                } else {
                    return type.getName();
                }
            }

            @Override
            public InventoryPlaceType fromString(String string) {
                return new InventoryPlaceType(string);
            }
        };
    }

    @Override
    protected void addType() {
        InventoryPlaceType newType = new InventoryPlaceType("new");
        getItems().add(newType);
    }

    @Override
    protected void removeTypes() {
        ObservableList<InventoryPlaceType> selectedItems = getSelectionModel().getSelectedItems();
        getItems().removeAll(selectedItems);
    }
}
