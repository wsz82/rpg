package io.wsz.model.item;

import io.wsz.model.asset.Asset;
import io.wsz.model.stage.Coords;

public class Cover extends Item {

    public Cover(Asset asset, Coords pos, int level) {
        super(asset, pos, level);
    }

    @Override
    public void update() {

    }
}
