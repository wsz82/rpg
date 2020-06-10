package io.wsz.model.plugin;

import io.wsz.model.item.ItemType;
import io.wsz.model.stage.Coords;

import java.util.List;

public class PosItemSerializable extends AssetSerializable {
    protected Coords pos;
    protected int level;
    protected boolean generic;

    public PosItemSerializable(String name, ItemType type, String path, Coords pos, int level, boolean generic,
                               List<Coords> coverLine, List<List<Coords>> collisionPolygons) {
        super(name, type, path, coverLine, collisionPolygons);
        this.pos = pos;
        this.level = level;
        this.generic = generic;
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

    public boolean isGeneric() {
        return generic;
    }

    public void setGeneric(boolean generic) {
        this.generic = generic;
    }
}
