package io.wsz.model.location;

import io.wsz.model.content.ItemList;
import io.wsz.model.item.PosItem;
import io.wsz.model.layer.Layer;
import io.wsz.model.layer.LayersList;
import javafx.beans.property.*;
import javafx.collections.ObservableList;

public class CurrentLocation {
    private static CurrentLocation singleton;
    private final ObjectProperty<Location> locationProperty = new SimpleObjectProperty<>();
    private final DoubleProperty width = new SimpleDoubleProperty();
    private final DoubleProperty height = new SimpleDoubleProperty();
    private final StringProperty name = new SimpleStringProperty();

    public static CurrentLocation get() {
        if (singleton == null) {
            singleton = new CurrentLocation();
        }
        return singleton;
    }

    private CurrentLocation(){
    }

    public ObservableList<Layer> getLayers() {
        return getLocation().getLayers().get();
    }

    public LayersList getLayersList() {
        return getLocation().getLayers();
    }

    public ObservableList<PosItem> getItems() {
        return getLocation().getItems().get();
    }

    public ItemList getItemsList() {
        return getLocation().getItems();
    }

    public void setLocation(Location location) {
        this.width.set(location.getWidth());
        this.height.set(location.getHeight());
        this.name.set(location.getName());
        this.locationProperty.set(location);
    }

    public Location getLocation() {
        return locationProperty.get();
    }

    public ObjectProperty<Location> locationProperty() {
        return locationProperty;
    }

    public double getWidth() {
        return width.get();
    }

    public DoubleProperty widthProperty() {
        return width;
    }

    public double getHeight() {
        return height.get();
    }

    public DoubleProperty heightProperty() {
        return height;
    }

    public void setWidth(double width) {
        this.locationProperty.get().setWidth(width);
        this.width.set(width);
    }

    public void setHeight(double height) {
        this.locationProperty.get().setHeight(height);
        this.height.set(height);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }
}
