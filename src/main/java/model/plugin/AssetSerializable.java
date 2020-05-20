package model.plugin;

import model.item.ItemType;

import java.io.Serializable;

class AssetSerializable implements Serializable {
    private String name;
    private ItemType type;
    private String path;

    AssetSerializable(String name, ItemType type, String path) {
        this.name = name;
        this.type = type;
        this.path = path;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    ItemType getType() {
        return type;
    }

    void setType(ItemType type) {
        this.type = type;
    }

    String getPath() {
        return path;
    }

    void setPath(String path) {
        this.path = path;
    }
}
