package io.wsz.model.plugin;

import io.wsz.model.item.ItemType;
import io.wsz.model.stage.Coords;

import java.util.List;

public class PosItemSerializable extends AssetSerializable {
    protected Coords pos;
    protected int level;

    public PosItemSerializable(String name, ItemType type, String path, Coords pos, int level,
                               Coords[] coverLine, List<List<Coords>> collisionPolygons) {
        super(name, type, path, coverLine, collisionPolygons);
        this.pos = pos;
        this.level = level;
    }

    public Coords getPos() {
        return pos;
    }

    public void setPos(Coords pos) {
        this.pos = pos;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
