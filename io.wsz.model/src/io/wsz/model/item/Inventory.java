package io.wsz.model.item;

import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import io.wsz.model.stage.Geometry;
import javafx.scene.image.Image;

import java.io.*;
import java.util.*;

public class Inventory implements Externalizable {
    private static final long serialVersionUID = 1L;

    private static final Coords TEMP = new Coords();

    private Creature owner;
    private Map<InventoryPlaceType, List<Coords>> inventoryPlaces;
    private List<Equipment> items;
    private Map<InventoryPlaceType, Equipment> equippedItems;

    public Inventory() {
        this.items = new ArrayList<>(0);
        this.equippedItems = new HashMap<>(0);
    }

    public Inventory(Creature owner) {
        this.owner = owner;
        this.items = new ArrayList<>(0);
        this.equippedItems = new HashMap<>(0);
    }

    public boolean tryAdd(Equipment equipment) {
        if (!fitsInventory(equipment)) {
            return false;
        }
        items.add(equipment);
        return true;
    }

    public boolean tryWear(Equipment equipment, double x, double y) {
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
        Equipment worn = equippedItems.get(placeToWear);
        if (worn == null) return;
        equippedItems.remove(placeToWear);
        items.add(worn);
    }

    public boolean tryTakeOff(Equipment toRemove) {
        for (InventoryPlaceType type : equippedItems.keySet()) {
            Equipment equipment = equippedItems.get(type);
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
        Image background = owner.getAnimation().getInventoryBasicForEditor(programDir);
        double width = background.getWidth() / Sizes.getMeter();
        double height = background.getHeight() / Sizes.getMeter();
        double relX = x / width;
        double relY = y / height;
        temp.x = relX;
        temp.y = relY;
        return Geometry.isInsidePolygon(temp, place);
    }

    public boolean fitsInventory(Equipment equipment) {
        double size = equipment.getSize();
        return getFilledSpace() + size < getMaxSize();
    }

    public void remove(Equipment e) {
        items.remove(e);
    }

    public int getMaxSize() {
        return (owner.getSize().ordinal() + 1) * 5;
    }

    public double getMaxWeight() {
        return owner.getStrength() * 3;
    }

    public int getFilledSpace() {
        return getEquippedItemsSize() + getItems().stream()
                .mapToInt(Equipment::getSize)
                .sum();
    }

    private int getEquippedItemsSize() {
        return equippedItems.values().stream()
                .mapToInt(Equipment::getSize)
                .sum();
    }

    public double getActualWeight() {
        return getEquippedItemsWeight() + getItems().stream()
                .mapToDouble(Equipment::getWeight)
                .sum();
    }

    private double getEquippedItemsWeight() {
        return equippedItems.values().stream()
                .mapToDouble(Equipment::getWeight)
                .sum();
    }

    public List<Equipment> getItems() {
        return items;
    }

    public Map<InventoryPlaceType, List<Coords>> getInventoryPlaces() {
        return inventoryPlaces;
    }

    public void setInventoryPlaces(Map<InventoryPlaceType, List<Coords>> inventoryPlaces) {
        this.inventoryPlaces = inventoryPlaces;
    }

    public Map<InventoryPlaceType, Equipment> getEquippedItems() {
        return equippedItems;
    }

    public void setEquippedItems(Map<InventoryPlaceType, Equipment> equippedItems) {
        this.equippedItems = equippedItems;
    }

    @Override
    public String toString() {
        return "Inventory{" +
                "owner=" + owner +
                ", inventoryPlaces=" + inventoryPlaces +
                ", items=" + items +
                ", equippedItems=" + equippedItems +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Inventory)) return false;
        Inventory inventory = (Inventory) o;
        return Objects.equals(getInventoryPlaces(), inventory.getInventoryPlaces()) &&
                Objects.equals(getItems(), inventory.getItems()) &&
                Objects.equals(getEquippedItems(), inventory.getEquippedItems());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getInventoryPlaces(), getItems(), getEquippedItems());
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(Sizes.VERSION);

        out.writeObject(owner);

        out.writeObject(inventoryPlaces);

        out.writeObject(items);

        out.writeObject(equippedItems);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        owner = (Creature) in.readObject();

        inventoryPlaces = (Map<InventoryPlaceType, List<Coords>>) in.readObject();

        items = (List<Equipment>) in.readObject();

        equippedItems = (Map<InventoryPlaceType, Equipment>) in.readObject();
    }
}
