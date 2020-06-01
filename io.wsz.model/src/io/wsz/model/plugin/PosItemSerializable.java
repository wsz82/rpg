package io.wsz.model.plugin;

import io.wsz.model.item.ItemType;

import java.io.Serializable;

public class PosItemSerializable implements Serializable {
    private String name;
    private ItemType type;
    private String path;
    private CoordinatesSerializable pos;
    private int level;

    public PosItemSerializable(String name, ItemType type, String path, CoordinatesSerializable pos, int level) {
        this.name = name;
        this.type = type;
        this.path = path;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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
