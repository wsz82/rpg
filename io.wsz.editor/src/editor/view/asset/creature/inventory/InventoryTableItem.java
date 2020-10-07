package editor.view.asset.creature.inventory;

import editor.view.asset.items.TableItem;
import io.wsz.model.item.Equipment;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.Objects;

public class InventoryTableItem extends TableItem {
    private final BooleanProperty isWorn = new SimpleBooleanProperty(false, "isWorn");

    public InventoryTableItem(Equipment<?,?> equipment, int count) {
        super(equipment, count);
    }

    public InventoryTableItem(Equipment<?,?> equipment, int count, boolean isWorn) {
        super(equipment, count);
        this.isWorn.setValue(isWorn);
    }

    public boolean isWorn() {
        return isWorn.get();
    }

    public BooleanProperty isWornProperty() {
        return isWorn;
    }

    public void setWorn(boolean isWorn) {
        this.isWorn.set(isWorn);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InventoryTableItem)) return false;
        if (!super.equals(o)) return false;
        InventoryTableItem that = (InventoryTableItem) o;
        return isWorn() == that.isWorn();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), isWorn());
    }
}
