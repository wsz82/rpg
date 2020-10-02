package io.wsz.model.item;

import io.wsz.model.animation.creature.CreatureAnimation;
import io.wsz.model.item.list.EquipmentList;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import io.wsz.model.stage.Geometry;
import io.wsz.model.stage.ResolutionImage;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Inventory implements Externalizable {
    private static final long serialVersionUID = 1L;

    private static final Coords TEMP = new Coords();

    private Creature owner;
    private Map<InventoryPlaceType, List<Coords>> inventoryPlaces;
    private EquipmentList equipmentList;
    private Map<InventoryPlaceType, Equipment<?,?>> equippedItems;

    public Inventory() {}

    public Inventory(Creature owner, EquipmentList equipmentList,
                     HashMap<InventoryPlaceType, Equipment<?, ?>> equippedItems) {
        this.owner = owner;
        this.equipmentList = equipmentList;
        this.equippedItems = equippedItems;
    }

    public boolean tryAdd(Equipment<?,?> equipment, boolean doMergeCountable) {
        if (!fitsInventory(equipment)) {
            return false;
        }
        if (equipment.isCountable() && doMergeCountable) {
            EquipmentMayCountable<?,?> countable = getEquipmentList().getEquipmentMayCountableList().getMergedList().stream()
                    .filter(e -> e.getAssetId().equals(equipment.getAssetId()))
                    .filter(e -> e.isUnitIdentical(equipment))
                    .findFirst().orElse(null);
            if (countable == null) {
                equipment.addItemToEquipmentList(equipmentList);
            } else {
                Integer addedAmount = equipment.getAmount();
                Integer alreadyInAmount = countable.getAmount();
                int sum = alreadyInAmount + addedAmount;
                countable.setAmount(sum);
            }
        } else {
            equipment.addItemToEquipmentList(equipmentList);
        }
        return true;
    }

    public boolean tryWear(Equipment<?,?> equipment, double x, double y) {
        if (!fitsInventory(equipment)) {
            return false;
        }
        InventoryPlaceType placeToWear = equipment.getOccupiedPlace();
        if (!fitsPlace(x, y, placeToWear)) {
            return false;
        }
        if (isPlaceOccupied(placeToWear)) {
            takeOff(placeToWear);
        }
        equippedItems.put(placeToWear, equipment);
        return true;
    }

    private void takeOff(InventoryPlaceType placeToWear) {
        Equipment<?,?> worn = equippedItems.get(placeToWear);
        if (worn == null) return;
        equippedItems.remove(placeToWear);
        worn.addItemToEquipmentList(equipmentList);
    }

    public boolean tryTakeOff(Equipment<?,?> toRemove) {
        for (InventoryPlaceType type : equippedItems.keySet()) {
            Equipment<?,?> equipment = equippedItems.get(type);
            if (equipment.equals(toRemove)) {
                equippedItems.remove(type);
                return true;
            }
        }
        return false;
    }

    private boolean isPlaceOccupied(InventoryPlaceType placeToWear) {
        return equippedItems.get(placeToWear) != null;
    }

    private boolean fitsPlace(double x, double y, InventoryPlaceType type) {
        Map<InventoryPlaceType, List<Coords>> inventoryPlaces = owner.getInventoryPlaces();
        boolean noPlaceForEquipment = !inventoryPlaces.containsKey(type);
        if (noPlaceForEquipment) {
            return false;
        }
        List<Coords> place = inventoryPlaces.get(type);
        return fitsPlace(x, y, owner, TEMP, place);
    }

    public boolean fitsPlace(double x, double y, Creature owner, Coords temp, List<Coords> place) {
        File programDir = owner.getController().getProgramDir();
        CreatureAnimation animation = owner.getAnimation();
        ResolutionImage background = animation.getInventoryBasicForEditor(programDir);
        double width = background.getWidth() / Sizes.getMeter();
        double height = background.getHeight() / Sizes.getMeter();
        double relX = x / width;
        double relY = y / height;
        temp.x = relX;
        temp.y = relY;
        return Geometry.isInsidePolygon(temp, place);
    }

    public boolean fitsInventory(Equipment<?,?> equipment) {
        double size = equipment.getSize();
        return getFilledSpace() + size < getMaxSize();
    }

    public void remove(Equipment<?,?> e) {
        e.removeItemFromEquipmentList(equipmentList);
    }

    public int getMaxSize() {
        return (owner.getSize().ordinal() + 1) * 5;
    }

    public double getMaxWeight() {
        return owner.getStrength() * 3;
    }

    public double getFilledSpace() {
        return getEquippedItemsSize() + getEquipmentList().getMergedList().stream()
                .mapToDouble(Equipment::getSize)
                .sum();
    }

    private double getEquippedItemsSize() {
        return equippedItems.values().stream()
                .mapToDouble(Equipment::getSize)
                .sum();
    }

    public double getActualWeight() {
        return getEquippedItemsWeight() + getEquipmentList().getMergedList().stream()
                .mapToDouble(Equipment::getWeight)
                .sum();
    }

    private double getEquippedItemsWeight() {
        return equippedItems.values().stream()
                .mapToDouble(Equipment::getWeight)
                .sum();
    }

    public EquipmentList getEquipmentList() {
        return equipmentList;
    }

    public void setEquipmentList(EquipmentList equipmentList) {
        this.equipmentList = equipmentList;
    }

    public Map<InventoryPlaceType, List<Coords>> getInventoryPlaces() {
        return inventoryPlaces;
    }

    public void setInventoryPlaces(Map<InventoryPlaceType, List<Coords>> inventoryPlaces) {
        this.inventoryPlaces = inventoryPlaces;
    }

    public Map<InventoryPlaceType, Equipment<?,?>> getEquippedItems() {
        return equippedItems;
    }

    public void setEquippedItems(Map<InventoryPlaceType, Equipment<?,?>> equippedItems) {
        this.equippedItems = equippedItems;
    }

    @Override
    public String toString() {
        return "Inventory{" +
                "owner=" + owner +
                ", inventoryPlaces=" + inventoryPlaces +
                ", items=" + equipmentList +
                ", equippedItems=" + equippedItems +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Inventory)) return false;
        Inventory inventory = (Inventory) o;
        return Objects.equals(getInventoryPlaces(), inventory.getInventoryPlaces()) &&
                Objects.equals(getEquipmentList(), inventory.getEquipmentList()) &&
                Objects.equals(getEquippedItems(), inventory.getEquippedItems());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getInventoryPlaces(), getEquipmentList(), getEquippedItems());
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(Sizes.VERSION);

        out.writeObject(owner);

        out.writeObject(inventoryPlaces);

        out.writeObject(equipmentList);

        out.writeObject(equippedItems);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        owner = (Creature) in.readObject();

        inventoryPlaces = (Map<InventoryPlaceType, List<Coords>>) in.readObject();

        equipmentList = (EquipmentList) in.readObject();

        equippedItems = (Map<InventoryPlaceType, Equipment<?,?>>) in.readObject();
    }
}
