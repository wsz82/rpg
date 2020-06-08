package io.wsz.model.item;

import io.wsz.model.stage.Coords;

public class Cover extends PosItem {

    public Cover(String name, ItemType type, String path, Coords pos, int level, Coords[] coverLine) {
        super(name, type, path, pos, level, coverLine);
    }

    @Override
    public void update() {

    }
}
