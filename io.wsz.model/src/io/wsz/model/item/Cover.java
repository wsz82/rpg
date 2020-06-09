package io.wsz.model.item;

import io.wsz.model.stage.Coords;

import java.util.List;

public class Cover extends PosItem {

    public Cover(String name, ItemType type, String path, Coords pos, int level,
                 Coords[] coverLine, List<List<Coords>> collisionPolygons) {
        super(name, type, path, pos, level, coverLine, collisionPolygons);
    }

    @Override
    public void update() {

    }
}
