package io.wsz.model.world;

import io.wsz.model.dialog.Dialog;
import io.wsz.model.item.EquipmentType;
import io.wsz.model.item.InventoryPlaceType;
import io.wsz.model.item.list.ItemsList;
import io.wsz.model.location.Location;
import io.wsz.model.script.Script;
import io.wsz.model.script.variable.Variables;
import io.wsz.model.sizes.Sizes;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

public class World implements Externalizable {
    private static final long serialVersionUID = 1L;

    private List<Location> locations;
    private ItemsList assets;
    private List<EquipmentType> equipmentTypes;
    private List<InventoryPlaceType> inventoryPlaceTypes;
    private List<Dialog> dialogs;
    private Variables variables;
    private List<Script> scripts;

    public World() {}

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public ItemsList getAssets() {
        return assets;
    }

    public void setAssets(ItemsList assets) {
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

    public Variables getVariables() {
        return variables;
    }

    public void setVariables(Variables variables) {
        this.variables = variables;
    }

    public List<Script> getScripts() {
        return scripts;
    }

    public void setScripts(List<Script> scripts) {
        this.scripts = scripts;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(Sizes.VERSION);

        out.writeObject(locations);

        out.writeObject(assets);

        out.writeObject(equipmentTypes);

        out.writeObject(inventoryPlaceTypes);

        out.writeObject(dialogs);

        out.writeObject(variables);

        out.writeObject(scripts);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        locations = (List<Location>) in.readObject();

        assets = (ItemsList) in.readObject();

        equipmentTypes = (List<EquipmentType>) in.readObject();

        inventoryPlaceTypes = (List<InventoryPlaceType>) in.readObject();

        dialogs = (List<Dialog>) in.readObject();

        variables = (Variables) in.readObject();

        scripts = (List<Script>) in.readObject();
    }
}
