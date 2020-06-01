package io.wsz.model.item;

import io.wsz.model.stage.Coords;

public class Landscape extends PosItem {

    public Landscape(String name, ItemType type, String path, Coords pos, int level) {
        super(name, type, path, pos, level);
    }

    @Override
    public void update() {

    }
}
