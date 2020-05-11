package model.layer;

import javafx.beans.property.*;

import java.util.HashSet;
import java.util.Set;

public class Layer implements LevelValueObservable {
    private Set<LevelValueListener> levelValueListeners = new HashSet<>();
    private final IntegerProperty level;
    private final StringProperty name;
    private final BooleanProperty visible;

    public Layer() {
        level = new SimpleIntegerProperty(this, "level");
        name = new SimpleStringProperty(this, "name");
        visible = new SimpleBooleanProperty(this, "visible");
    }

    public Layer(String layerName) {
        level = new SimpleIntegerProperty();
        level.set(0);
        name = new SimpleStringProperty();
        name.set(layerName);
        visible = new SimpleBooleanProperty();
        visible.set(true);
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
    public void attach(LevelValueListener listener) {
        levelValueListeners.add(listener);
    }

    @Override
    public void remove(LevelValueListener listener) {
        levelValueListeners.remove(listener);
    }

    @Override
    public void notify(int oldValue, int newValue) {
        levelValueListeners.forEach(listener -> listener.onLevelValueChanged(oldValue, newValue));
    }
}
