package io.wsz.model.plugin;

import io.wsz.model.item.ItemType;
import io.wsz.model.stage.Coords;

import java.io.Serializable;
import java.util.List;

public class AssetSerializable implements Serializable {
    private String name;
    private ItemType type;
    private String path;
    private Coords[] coverLine;
    private List<List<Coords>> collisionPolygons;

    public AssetSerializable(String name, ItemType type, String path,
                             Coords[] coverLine, List<List<Coords>> collisionPolygons) {
        this.name = name;
        this.type = type;
        this.path = path;
        this.coverLine = coverLine;
        this.collisionPolygons = collisionPolygons;
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

    public List<List<Coords>> getCollisionPolygons() {
        return collisionPolygons;
    }

    public void setCollisionPolygons(List<List<Coords>> collisionPolygons) {
        this.collisionPolygons = collisionPolygons;
    }

    @Override
    public String toString() {
        return getName();
    }
}
