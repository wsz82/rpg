package io.wsz.model.item;

import io.wsz.model.stage.Coords;

public class Teleport extends PosItem {
    private String locationName;
    private Coords exit;
    private int exitLevel;

    public Teleport(String name, ItemType type, String path, Coords pos, int level) {
        super(name, type, path, pos, level);
    }

    public Teleport(String name, ItemType type, String path, Coords pos, int level,
                    String locationName, Coords exitPos, int exitLevel) {
        super(name, type, path, pos, level);
        this.locationName = locationName;
        this.exit = exitPos;
        this.exitLevel = exitLevel;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Coords getExit() {
        return exit;
    }

    public void setExit(Coords exit) {
        this.exit = exit;
    }

    public int getExitLevel() {
        return exitLevel;
    }

    public void setExitLevel(int exitLevel) {
        this.exitLevel = exitLevel;
    }

    @Override
    public void update() {

    }
}
