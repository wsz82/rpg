package model.layer;

import javafx.beans.property.*;

public class Layer {
    private final IntegerProperty level;
    private final StringProperty name;
    private final BooleanProperty visible;

    public Layer() {
        this.level = new SimpleIntegerProperty(this, "level");
        this.name = new SimpleStringProperty(this, "name");
        this.visible = new SimpleBooleanProperty(this, "visible");
    }

    public Layer(String layerName) {
        this.level = new SimpleIntegerProperty();
        this.level.set(0);
        this.name = new SimpleStringProperty();
        this.name.set(layerName);
        this.visible = new SimpleBooleanProperty();
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
}
