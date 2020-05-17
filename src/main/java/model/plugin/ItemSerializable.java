package model.plugin;

import model.item.ItemType;

import java.io.Serializable;

public class ItemSerializable implements Serializable {
    private String name;
    private ItemType type;
    private CoordinatesSerializable pos;
    private int level;

    public ItemSerializable(String name, ItemType type, CoordinatesSerializable pos, int level) {
        this.name = name;
        this.type = type;
        this.pos = pos;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }

    public CoordinatesSerializable getPos() {
        return pos;
    }

    public void setPos(CoordinatesSerializable pos) {
        this.pos = pos;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
