package io.wsz.model.world;

import io.wsz.model.asset.Asset;
import io.wsz.model.dialog.Dialog;
import io.wsz.model.item.EquipmentType;
import io.wsz.model.item.InventoryPlaceType;
import io.wsz.model.location.Location;
import io.wsz.model.sizes.Sizes;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

public class World implements Externalizable {
    private static final long serialVersionUID = 1L;

    private List<Location> locations;
    private List<Asset> assets;
    private List<EquipmentType> equipmentTypes;
    private List<InventoryPlaceType> inventoryPlaceTypes;
    private List<Dialog> dialogs;

    public World() {}

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public List<Asset> getAssets() {
        return assets;
    }

    public void setAssets(List<Asset> assets) {
        this.assets = assets;
    }

    public List<EquipmentType> getEquipmentTypes() {
        return equipmentTypes;
    }

    public void setEquipmentTypes(List<EquipmentType> equipmentTypes) {
        this.equipmentTypes = equipmentTypes;
    }

    public List<InventoryPlaceType> getInventoryPlaces() {
        return inventoryPlaceTypes;
    }

    public void setInventoryPlaces(List<InventoryPlaceType> inventoryPlaceTypes) {
        this.inventoryPlaceTypes = inventoryPlaceTypes;
    }

    public List<Dialog> getDialogs() {
        return dialogs;
    }

    public void setDialogs(List<Dialog> dialogs) {
        this.dialogs = dialogs;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(Sizes.VERSION);

        out.writeObject(locations);

        out.writeObject(assets);

        out.writeObject(equipmentTypes);

        out.writeObject(inventoryPlaceTypes);

        out.writeObject(dialogs);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        locations = (List<Location>) in.readObject();

        assets = (List<Asset>) in.readObject();

        equipmentTypes = (List<EquipmentType>) in.readObject();

        inventoryPlaceTypes = (List<InventoryPlaceType>) in.readObject();

        dialogs = (List<Dialog>) in.readObject();
    }
}
