package model.stage;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import model.layer.Layer;

public class CurrentLayer {
    private final ObjectProperty<Layer> currentLayer = new SimpleObjectProperty<>();
    private final IntegerProperty currentLevel = new SimpleIntegerProperty();
    private static CurrentLayer singleton;

    public static CurrentLayer get() {
        if (singleton == null) {
            singleton = new CurrentLayer();
        }
        return singleton;
    }

    private CurrentLayer() {
    }

    public ObjectProperty<Layer> currentLayerProperty() {
        return currentLayer;
    }

    public Layer getCurrentLayer() {
        return currentLayer.get();
    }

    public void setCurrentLayer(Layer layer) {
        currentLevel.set(layer.getLevel());
        currentLayer.set(layer);
    }

    public int getCurrentLevel() {
        return currentLevel.get();
    }

    public IntegerProperty currentLevelProperty() {
        return currentLevel;
    }
}
