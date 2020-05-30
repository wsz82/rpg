package io.wsz.model.layer;

import javafx.beans.property.*;

import java.util.Objects;

public class Layer {
    private final IntegerProperty level = new SimpleIntegerProperty(this, "level");
    private final StringProperty name = new SimpleStringProperty(this, "name");
    private final BooleanProperty visible = new SimpleBooleanProperty(this, "visible");

    public Layer() {}

    public Layer(String layerName) {
        this.level.set(0);
        this.name.set(layerName);
        this.visible.set(true);
    }

    public void setLevel(int value) {
        level.set(value);
    }

    public int getLevel() {
        return level.get();
    }

    public void setName(String value) {
        name.set(value);
    }

    public String getName() {
        return name.get();
    }

    public void setVisible(boolean value) {
        visible.set(value);
    }

    public boolean getVisible() {
        return visible.get();
    }

    public BooleanProperty getVisibleProperty() {
        return visible;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Layer layer = (Layer) o;
        return level.equals(layer.level) &&
                name.equals(layer.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(level, name);
    }
}
