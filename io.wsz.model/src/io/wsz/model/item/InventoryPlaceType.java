package io.wsz.model.item;

import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import io.wsz.model.stage.Geometry;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class InventoryPlaceType implements Externalizable {
    private static final long serialVersionUID = 1L;

    public static Map<InventoryPlaceType, List<Coords>> cloneInventoryPlacesKeysWithValues(Map<InventoryPlaceType, List<Coords>> other) {
        Map<InventoryPlaceType, List<Coords>> clone = new HashMap<>(0);
        for (InventoryPlaceType type : other.keySet()) {
            InventoryPlaceType clonedType = new InventoryPlaceType(type.getId());
            List<Coords> otherPolygon = other.get(type);
            List<Coords> clonedPolygon = Geometry.cloneCoordsList(otherPolygon);
            clone.put(clonedType, clonedPolygon);
        }
        return clone;
    }

    private String id;

    public InventoryPlaceType() {}

    public InventoryPlaceType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InventoryPlaceType)) return false;
        InventoryPlaceType type = (InventoryPlaceType) o;
        return Objects.equals(getId(), type.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(Sizes.VERSION);

        out.writeUTF(id);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        id = in.readUTF();
    }
}
