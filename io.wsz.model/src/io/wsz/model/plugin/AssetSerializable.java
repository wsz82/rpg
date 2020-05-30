package io.wsz.model.plugin;

import io.wsz.model.item.ItemType;

import java.io.Serializable;

public class AssetSerializable implements Serializable {
    private String name;
    private ItemType type;
    private String path;

    public AssetSerializable(String name, ItemType type, String path) {
        this.name = name;
        this.type = type;
        this.path = path;
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
}
