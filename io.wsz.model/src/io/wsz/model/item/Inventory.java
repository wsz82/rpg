package io.wsz.model.item;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Inventory implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Equipment> items = new ArrayList<>(1);
    private Creature owner;
    private Weapon equippedWeapon;

    public Inventory() {}

    public Inventory(Creature owner) {
        this.items = new ArrayList<>(1);
        this.owner = owner;
    }

    public boolean add(Equipment equipment) {
        double size = equipment.getSize();
        if (getFilledSpace() + size > getMaxSize()) {
            return false;
        }
        items.add(equipment);
        return true;
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

    public Weapon getEquippedWeapon() {
        return equippedWeapon;
    }

    public void setEquippedWeapon(Weapon equippedWeapon) {
        this.equippedWeapon = equippedWeapon;
    }

    public List<Equipment> getItems() {
        return items;
    }

    public int getFilledSpace() {
        return getItems().stream()
                .mapToInt(Equipment::getSize)
                .sum();
    }

    public double getActualWeight() {
        return getItems().stream()
                .mapToDouble(Equipment::getWeight)
                .sum();
    }

    @Override
    public String toString() {
        return "Inventory{" +
                "items=" + items +
                ", owner=" + owner +
                ", equippedWeapon=" + equippedWeapon +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Inventory)) return false;
        Inventory inventory = (Inventory) o;
        return Objects.equals(getItems(), inventory.getItems()) &&
                Objects.equals(getEquippedWeapon(), inventory.getEquippedWeapon());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getItems(), getEquippedWeapon());
    }
}
