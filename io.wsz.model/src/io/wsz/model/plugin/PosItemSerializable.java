package io.wsz.model.plugin;

import io.wsz.model.item.ItemType;
import io.wsz.model.stage.Coords;

public class PosItemSerializable extends AssetSerializable {
    protected Coords pos;
    protected int level;
    protected Coords[] coverLine;

    public PosItemSerializable(String name, ItemType type, String path, Coords pos, int level, Coords[] coverLine) {
        super(name, type, path);
        this.pos = pos;
        this.level = level;
        this.coverLine = coverLine;
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

    public Coords[] getCoverLine() {
        return coverLine;
    }

    public void setCoverLine(Coords[] coverLine) {
        this.coverLine = coverLine;
    }
}
