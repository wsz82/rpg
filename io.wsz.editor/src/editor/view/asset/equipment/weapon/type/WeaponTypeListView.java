package editor.view.asset.equipment.weapon.type;

import editor.model.EditorController;
import editor.view.asset.TypeListView;
import io.wsz.model.item.WeaponType;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;

public class WeaponTypeListView extends TypeListView<WeaponType> {

    public WeaponTypeListView(EditorController editorController) {
        super(editorController);
    }

    @Override
    protected void setTypeItems() {
        ObservableList<WeaponType> items = editorController.getObservableWeaponTypes();
        setItems(items);
    }

    @Override
    protected StringConverter<WeaponType> getStringConverter() {
        StringConverter<WeaponType> stringConverter = new StringConverter<>() {
            @Override
            public String toString(WeaponType weaponType) {
                return weaponType.getName();
            }

            @Override
            public WeaponType fromString(String string) {
                return new WeaponType(string);
            }
        };
        return stringConverter;
    }

    @Override
    protected void addType() {
        WeaponType newType = new WeaponType("new");
        getItems().add(newType);
    }

    @Override
    protected void removeTypes() {
        ObservableList<WeaponType> selectedItems = getSelectionModel().getSelectedItems();
        getItems().removeAll(selectedItems);
    }
}
