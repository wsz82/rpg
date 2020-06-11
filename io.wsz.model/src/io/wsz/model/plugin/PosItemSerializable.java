package io.wsz.model.plugin;

import io.wsz.model.item.ItemType;
import io.wsz.model.stage.Coords;

import java.util.List;

public class PosItemSerializable extends AssetSerializable {
    protected String prototype;
    protected Boolean visible;
    protected Coords pos;
    protected Integer level;

    public PosItemSerializable(String prototype, String name, ItemType type, String path,
                              Boolean visible, Coords pos, Integer level,
                               List<Coords> coverLine, List<List<Coords>> collisionPolygons) {
        super(name, type, path, coverLine, collisionPolygons);
        this.visible = visible;
        this.pos = pos;
        this.level = level;
        this.prototype = prototype;
    }

    public Coords getPos() {
        return pos;
    }

    public void setPos(Coords pos) {
        this.pos = pos;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getPrototype() {
        return prototype;
    }

    public void setPrototype(String prototype) {
        this.prototype = prototype;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }
}
