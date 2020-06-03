package io.wsz.model.item;

import io.wsz.model.stage.Coords;

public class Obstacle extends PosItem {

    public Obstacle(String name, ItemType type, String path, Coords coords, int level) {
        super(name, type, path, coords, level);
    }

    @Override
    public void update() {

    }
}
