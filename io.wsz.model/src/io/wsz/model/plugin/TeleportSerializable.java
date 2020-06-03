package io.wsz.model.plugin;

import io.wsz.model.item.ItemType;
import io.wsz.model.stage.Coords;

public class TeleportSerializable extends PosItemSerializable {
    private String locationName;
    private Coords exit;
    private int exitLevel;

    public TeleportSerializable(String name, ItemType type, String path, Coords exit, int exitLevel) {
        super(name, type, path, exit, exitLevel);
    }

    public TeleportSerializable(String name, ItemType type, String path, Coords exit, int exitLevel,
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
}
