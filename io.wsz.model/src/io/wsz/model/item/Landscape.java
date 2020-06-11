package io.wsz.model.item;

import io.wsz.model.stage.Coords;

import java.util.List;

public class Landscape extends PosItem {

    public Landscape(Asset prototype, String name, ItemType type, String path, Coords pos, Integer level,
                     List<Coords> coverLine, List<List<Coords>> collisionPolygons) {
        super(prototype, name, type, path, pos, level, coverLine, collisionPolygons);
    }

    @Override
    public void update() {

    }
}
