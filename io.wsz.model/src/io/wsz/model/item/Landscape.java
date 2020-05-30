package io.wsz.model.item;

import io.wsz.model.asset.Asset;
import io.wsz.model.stage.Coords;

public class Landscape extends Item {

    public Landscape(Asset asset, Coords pos, int level) {
        super(asset, pos, level);
    }

    @Override
    public void update() {

    }
}
