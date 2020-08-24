package io.wsz.model.item;

import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;
import java.util.Objects;

public class InventoryPlace implements Externalizable {
    private static final long serialVersionUID = 1L;

    private List<Coords> polygon;
    private boolean isOccupied;

    public InventoryPlace() {}

    public InventoryPlace(List<Coords> polygon) {
        this.polygon = polygon;
    }

    public List<Coords> getPolygon() {
        return polygon;
    }

    public void setPolygon(List<Coords> polygon) {
        this.polygon = polygon;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InventoryPlace)) return false;
        InventoryPlace that = (InventoryPlace) o;
        return isOccupied() == that.isOccupied() &&
                Objects.equals(getPolygon(), that.getPolygon());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPolygon(), isOccupied());
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(Sizes.VERSION);

        out.writeObject(polygon);

        out.writeBoolean(isOccupied);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        polygon = (List<Coords>) in.readObject();

        isOccupied = in.readBoolean();
    }
}
