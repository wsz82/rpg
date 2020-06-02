package io.wsz.model.plugin;

import io.wsz.model.item.ItemType;

public class PosItemSerializable extends AssetSerializable {
    protected CoordsSerializable pos;
    protected int level;

    public PosItemSerializable(String name, ItemType type, String path, CoordsSerializable pos, int level) {
        super(name, type, path);
        this.pos = pos;
        this.level = level;
    }

    public CoordsSerializable getPos() {
        return pos;
    }

    public void setPos(CoordsSerializable pos) {
        this.pos = pos;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
