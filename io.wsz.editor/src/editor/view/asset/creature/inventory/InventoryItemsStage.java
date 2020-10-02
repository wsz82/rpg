package editor.view.asset.creature.inventory;

import editor.model.EditorController;
import editor.view.asset.items.ItemsStage;
import editor.view.stage.EditorCanvas;
import io.wsz.model.item.Creature;
import io.wsz.model.item.Equipment;
import io.wsz.model.item.InventoryPlaceType;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InventoryItemsStage extends ItemsStage<Creature, InventoryTableItem> {

    public InventoryItemsStage(Stage parent, Creature creature, EditorCanvas editorCanvas, EditorController editorController) {
        super(parent, creature, editorCanvas, editorController);
    }

    @Override
    public void initWindow() {
        super.initWindow();
        initInventoryItemsTable();
    }

    private void initInventoryItemsTable() {
        Set<InventoryPlaceType> creatureAvailablePlaces = containable.getInventoryPlaces().keySet();

        TableColumn<InventoryTableItem, Boolean> wearCol = new TableColumn<>("Do wear");
        wearCol.setCellValueFactory(new PropertyValueFactory<>("isWorn"));
        wearCol.setEditable(true);
        wearCol.setCellFactory(CheckBoxTableCell.forTableColumn(p -> {
            InventoryTableItem item = tableItems.get(p);
            Equipment<?,?> equipment = item.getEquipment();
            InventoryPlaceType occupiedPlace = equipment.getOccupiedPlace();
            boolean cannotBeWorn = !creatureAvailablePlaces.contains(occupiedPlace);
            if (cannotBeWorn) {
                item.setWorn(false);
            }
            return item.isWornProperty();
        }));
        table.getColumns().add(wearCol);
    }

    @Override
    protected InventoryTableItem getNewEquipment(Equipment<?,?> e) {
        int count = getCount(e);
        return new InventoryTableItem(e, count);
    }

    @Override
    protected void save() {
        Map<InventoryPlaceType, Equipment<?,?>> equippedItems = containable.getInventory().getEquippedItems();
        equippedItems.clear();
        super.save();
    }

    @Override
    protected ObservableList<InventoryTableItem> getTableItems(List<Equipment<?,?>> items) {
        tableItems = super.getTableItems(items);
        Map<InventoryPlaceType, Equipment<?,?>> equippedItems = containable.getInventory().getEquippedItems();
        for (InventoryPlaceType placeType : equippedItems.keySet()) {
            Equipment<?,?> e = equippedItems.get(placeType);
            addEquippedItem(e);
        }
        return tableItems;
    }

    private void addEquippedItem(Equipment<?,?> e) {
        InventoryTableItem ti = new InventoryTableItem(e, 1, true);
        tableItems.add(ti);
        if (table != null) {
            table.refresh();
        }
    }

    @Override
    protected List<Equipment<?,?>> tableItemsToEquipment(ObservableList<InventoryTableItem> items) {
        List<Equipment<?,?>> output = new ArrayList<>(0);
        for (InventoryTableItem ti : items) {
            Equipment<?,?> tiEquipment = ti.getEquipment();
            int count = ti.getCount();
            boolean isWorn = ti.isWorn();
            if (isWorn) {
                addEquippedItem(output, count, tiEquipment);
            } else {
                addCountableOrSingle(output, tiEquipment, count);
            }
        }
        return output;
    }

    private void addEquippedItem(List<Equipment<?,?>> output, int count, Equipment<?,?> toAdd) {
        InventoryPlaceType occupiedPlace = toAdd.getOccupiedPlace();
        Map<InventoryPlaceType, Equipment<?,?>> equippedItems = containable.getInventory().getEquippedItems();
        if (equippedItems.get(occupiedPlace) != null && doInventoryHasThePlace(occupiedPlace)) {
            addCountableOrSingle(output, toAdd, count);
        } else {
            equippedItems.put(occupiedPlace, toAdd);
            boolean isNotCountable = !toAdd.isCountable();
            if (isNotCountable && count > 1) {
                addCountableOrSingle(output, toAdd, count - 1);
            }
        }
    }

    private boolean doInventoryHasThePlace(InventoryPlaceType occupiedPlace) {
        return containable.getInventoryPlaces().containsKey(occupiedPlace);
    }
}
