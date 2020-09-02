package editor.view.asset.equipment;

import editor.model.EditorController;
import editor.view.asset.AssetsTableView;
import editor.view.stage.EditorCanvas;
import io.wsz.model.item.Equipment;
import io.wsz.model.item.EquipmentType;
import io.wsz.model.item.InventoryPlaceType;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

public abstract class EquipmentTableView<E extends Equipment<?,?>> extends AssetsTableView<E> {

    public EquipmentTableView(Stage parent, ObservableList<E> assets,
                              EditorCanvas editorCanvas, EditorController editorController) {
        super(parent, assets, editorCanvas, editorController);
        initEquipmentsTable();
    }

    private void initEquipmentsTable() {
        TableColumn<E, String> placeCol = new TableColumn<>("Place");
        placeCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                InventoryPlaceType occupiedPlace = p.getValue().getOccupiedPlace();
                if (occupiedPlace == null) {
                    return "";
                } else {
                    return occupiedPlace.getId();
                }
            }
        });
        placeCol.setCellFactory(TextFieldTableCell.forTableColumn());
        placeCol.setEditable(false);
        getColumns().add(placeCol);

        TableColumn<E, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                EquipmentType equipmentType = p.getValue().getEquipmentType();
                if (equipmentType == null) {
                    return null;
                } else {
                    return equipmentType.getId();
                }
            }
        });
        typeCol.setCellFactory(TextFieldTableCell.forTableColumn());
        typeCol.setEditable(false);
        getColumns().add(typeCol);
    }
}
