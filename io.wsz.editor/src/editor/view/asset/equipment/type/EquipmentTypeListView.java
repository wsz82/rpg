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
    }

    @Override
    protected StringConverter<EquipmentType> getStringConverter() {
        StringConverter<EquipmentType> stringConverter = new StringConverter<>() {
            @Override
            public String toString(EquipmentType weaponType) {
                return weaponType.getName();
            }

            @Override
            public EquipmentType fromString(String string) {
                return new EquipmentType(string);
            }
        };
        return stringConverter;
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
