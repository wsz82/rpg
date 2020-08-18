package io.wsz.model.layer;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

public class CurrentLayer {
    private final ObjectProperty<Layer> layer = new SimpleObjectProperty<>();
    private final IntegerProperty level = new SimpleIntegerProperty();

    public CurrentLayer() {}

    public Layer getLayer() {
        return layer.get();
    }

    public void setLayer(Layer layer) {
        level.set(layer.getLevel());
        this.layer.set(layer);
    }

    public int getLevel() {
        return level.get();
    }

    public IntegerProperty levelProperty() {
        return level;
    }
}
