package model.item;

import model.asset.Asset;
import model.stage.Coords;

public abstract class Item {
    protected Asset asset;
    protected Coords pos;
    protected int level;

    public Item(Asset asset, Coords pos, int level) {
        this.asset = asset;
        this.pos = pos;
        this.level = level;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
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
