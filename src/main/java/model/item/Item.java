package model.item;

import model.stage.Coordinates;

public abstract class Item {
    protected String name;
    protected ItemType type;
    protected Coordinates coords;
    protected int level;

    public Item(String name, ItemType type, Coordinates coords, int level) {
        this.name = name;
        this.type = type;
        this.coords = coords;
        this.level = level;
    }

    public Coordinates getCoords() {
        return coords;
    }

    public void setCoords(Coordinates coords) {
        this.coords = coords;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
