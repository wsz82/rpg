package editor.view.asset.items;

import io.wsz.model.item.Equipment;

import java.util.Objects;

public class TableItem {
    private final Equipment<?,?> equipment;
    private int count;

    public TableItem(Equipment<?,?> equipment, int count) {
        this.equipment = equipment;
        this.count = count;
    }

    public Equipment<?,?> getEquipment() {
        return equipment;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "TableItem{" +
                "equipment=" + equipment +
                ", count=" + count +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TableItem)) return false;
        TableItem tableItem = (TableItem) o;
        return getCount() == tableItem.getCount() &&
                Objects.equals(getEquipment(), tableItem.getEquipment());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEquipment(), getCount());
    }
}
