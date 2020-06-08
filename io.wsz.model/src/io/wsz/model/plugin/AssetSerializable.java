package io.wsz.model.plugin;

import io.wsz.model.item.ItemType;
import io.wsz.model.stage.Coords;

import java.io.Serializable;

public class AssetSerializable implements Serializable {
    private String name;
    private ItemType type;
    private String path;
    protected Coords[] coverLine;

    public AssetSerializable(String name, ItemType type, String path, Coords[] coverLine) {
        this.name = name;
        this.type = type;
        this.path = path;
        this.coverLine = coverLine;
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

    public Coords[] getCoverLine() {
        return coverLine;
    }

    public void setCoverLine(Coords[] coverLine) {
        this.coverLine = coverLine;
    }

    @Override
    public String toString() {
        return getName();
    }
}
