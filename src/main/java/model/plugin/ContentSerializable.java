package model.plugin;

import java.io.Serializable;

class ContentSerializable implements Serializable {
    private ItemSerializable item;
    private boolean visible;

    ContentSerializable(ItemSerializable item, boolean visible) {
        this.item = item;
        this.visible = visible;
    }

    ItemSerializable getItem() {
        return item;
    }

    void setItem(ItemSerializable item) {
        this.item = item;
    }

    boolean isVisible() {
        return visible;
    }

    void setVisible(boolean visible) {
        this.visible = visible;
    }
}
