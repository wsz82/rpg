package io.wsz.model.item;

import io.wsz.model.sizes.Sizes;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Inventory implements Externalizable {
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
        if (fitsInventory(equipment)) {
            items.add(equipment);
            return true;
        }
        return false;
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
        return getEquippedWeaponSize() + getItems().stream()
                .mapToInt(Equipment::getSize)
                .sum();
    }

    private int getEquippedWeaponSize() {
        if (equippedWeapon == null) {
            return 0;
        } else {
            return equippedWeapon.getSize();
        }
    }

    public double getActualWeight() {
        return getEquippedWeaponWeight() + getItems().stream()
                .mapToDouble(Equipment::getWeight)
                .sum();
    }

    private double getEquippedWeaponWeight() {
        if (equippedWeapon == null) {
            return 0;
        } else {
            return equippedWeapon.getWeight();
        }
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

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(Sizes.VERSION);

        out.writeObject(items);

        out.writeObject(owner);

        out.writeObject(equippedWeapon);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        List<Equipment> serItems = (List<Equipment>) in.readObject();
        items.addAll(serItems);

        owner = (Creature) in.readObject();

        equippedWeapon = (Weapon) in.readObject();
    }
}
