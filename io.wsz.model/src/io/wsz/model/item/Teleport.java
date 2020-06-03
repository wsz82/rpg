package io.wsz.model.item;

import io.wsz.model.stage.Coords;

public class Teleport extends PosItem {
    private String locationName;
    private Coords exit;
    private int exitLevel;

    public Teleport(String name, ItemType type, String path, Coords coords, int exitLevel) {
        super(name, type, path, coords, exitLevel);
    }

    public Teleport(String name, ItemType type, String path, Coords exit, int exitLevel,
                    String locationName, Coords pos1, int level1) {
        super(name, type, path, exit, exitLevel);
        this.locationName = locationName;
        this.exit = pos1;
        this.exitLevel = level1;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    @Override
    public Coords getPos() {
        return exit;
    }

    @Override
    public void setPos(Coords exit) {
        this.exit = exit;
    }

    @Override
    public int getLevel() {
        return exitLevel;
    }

    @Override
    public void setLevel(int exitLevel) {
        this.exitLevel = exitLevel;
    }

    @Override
    public void update() {

    }
}
