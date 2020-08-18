package io.wsz.model.location;

import io.wsz.model.item.PosItem;
import io.wsz.model.layer.Layer;
import javafx.beans.property.*;
import javafx.collections.ObservableList;

public class CurrentLocation {
    private final ObjectProperty<Location> locationProperty = new SimpleObjectProperty<>();
    private final DoubleProperty widthProperty = new SimpleDoubleProperty();
    private final DoubleProperty heightProperty = new SimpleDoubleProperty();
    private final StringProperty nameProperty = new SimpleStringProperty();

    public CurrentLocation(){}

    public void setLocation(Location location) {
        this.widthProperty.set(location.getWidth());
        this.heightProperty.set(location.getHeight());
        this.nameProperty.set(location.getName());
        this.locationProperty.set(location);
    }

    public Location getLocation() {
        return locationProperty.get();
    }

    public ObjectProperty<Location> locationProperty() {
        return locationProperty;
    }

    public double getWidth() {
        return widthProperty.get();
    }

    public DoubleProperty getWidthProperty() {
        return widthProperty;
    }

    public void setWidth(double width) {
        this.locationProperty.get().setWidth(width);
        this.widthProperty.set(width);
    }

    public double getHeight() {
        return heightProperty.get();
    }

    public DoubleProperty getHeightProperty() {
        return heightProperty;
    }

    public void setHeight(double height) {
        this.locationProperty.get().setHeight(height);
        this.heightProperty.set(height);
    }

    public String getName() {
        return nameProperty.get();
    }

    public StringProperty getNameProperty() {
        return nameProperty;
    }

    public void setName(String name) {
        this.locationProperty.get().setName(name);
        this.nameProperty.set(name);
    }

    public ObservableList<PosItem> getItems() {
        return locationProperty.get().getItems();
    }

    public ObservableList<Layer> getLayers() {
        return locationProperty.get().getLayers();
    }
}
