package editor.view.layer;

import io.wsz.model.layer.Layer;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class CurrentObservableLayer {
    private final IntegerProperty level = new SimpleIntegerProperty();
    private Layer layer;

    public CurrentObservableLayer() {}

    public void saveCurrent() {
        if (layer == null) return;
        layer.setLevel(level.get());
    }

    public Layer getLayer() {
        return layer;
    }

    public void setLayer(Layer layer) {
        saveCurrent();
        if (layer != null) {
            this.level.set(layer.getLevel());
            this.layer = layer;
        } else {
            this.level.set(0);
            this.layer = null;
        }
    }

    public int getLevel() {
        return level.get();
    }

    public IntegerProperty levelProperty() {
        return level;
    }
}
