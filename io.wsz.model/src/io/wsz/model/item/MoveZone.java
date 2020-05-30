package io.wsz.model.item;

import io.wsz.model.asset.Asset;
import io.wsz.model.stage.Coords;

public class MoveZone extends Item {

    public MoveZone(Asset asset, Coords coords, int level) {
        super(asset, coords, level);
    }

    @Override
    public void update() {

    }
}
