package io.wsz.model.item;

import io.wsz.model.stage.Coords;

public abstract class PosItem extends Asset implements ItemUpdater {
    protected Coords pos;
    protected int level;

    public PosItem(String name, ItemType type, String path, Coords pos, int level) {
        super(name, type, path);
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
