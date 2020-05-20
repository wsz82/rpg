package model.plugin;

import java.io.Serializable;

class ItemSerializable implements Serializable {
    private String name;
    private CoordinatesSerializable pos;
    private int level;

    ItemSerializable(String name, CoordinatesSerializable pos, int level) {
        this.name = name;
        this.pos = pos;
        this.level = level;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    CoordinatesSerializable getPos() {
        return pos;
    }

    void setPos(CoordinatesSerializable pos) {
        this.pos = pos;
    }

    int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
