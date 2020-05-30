package io.wsz.model.item;

import io.wsz.model.asset.Asset;
import io.wsz.model.stage.Coords;

public abstract class Item implements ItemUpdater {
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
