package editor.view.asset.equipment.type;

import editor.model.EditorController;
import editor.view.asset.TypeListView;
import io.wsz.model.item.EquipmentType;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;

public class EquipmentTypeListView extends TypeListView<EquipmentType> {

    public EquipmentTypeListView(EditorController editorController) {
        super(editorController);
    }

    @Override
    protected void setTypeItems() {
        ObservableList<EquipmentType> items = editorController.getObservableEquipmentTypes();
        setItems(items);
        setOnEditCommit(e -> {
            String name = e.getNewValue().getId();
            EquipmentType type = getSelectionModel().getSelectedItem();
            type.setId(name);
        });
    }

    @Override
    protected StringConverter<EquipmentType> getStringConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(EquipmentType type) {
                if (type == null) {
                    return "";
                } else {
                    return type.getId();
                }
            }

            @Override
            public EquipmentType fromString(String string) {
                return new EquipmentType(string);
            }
        };
    }

    @Override
    protected void addType() {
        EquipmentType newType = new EquipmentType("new");
        getItems().add(newType);
    }

    @Override
    protected void removeTypes() {
        ObservableList<EquipmentType> selectedItems = getSelectionModel().getSelectedItems();
        getItems().removeAll(selectedItems);
    }
}
