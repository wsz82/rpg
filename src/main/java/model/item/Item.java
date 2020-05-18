package model.item;

import model.asset.Asset;
import model.stage.Coordinates;

public abstract class Item {
    protected Asset asset;
    protected Coordinates pos;
    protected int level;

    public Item(Asset asset, Coordinates pos, int level) {
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

    public Coordinates getPos() {
        return pos;
    }

    public void setPos(Coordinates pos) {
        this.pos = pos;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
