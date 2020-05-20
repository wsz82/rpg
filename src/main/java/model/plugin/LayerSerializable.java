package model.plugin;

import java.io.Serializable;

class LayerSerializable implements Serializable {
    private int level;
    private String name;
    private boolean visible;

    LayerSerializable(int level, String name, boolean visible) {
        this.level = level;
        this.name = name;
        this.visible = visible;
    }

    int getLevel() {
        return level;
    }

    void setLevel(int level) {
        this.level = level;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    boolean isVisible() {
        return visible;
    }

    void setVisible(boolean visible) {
        this.visible = visible;
    }
}
