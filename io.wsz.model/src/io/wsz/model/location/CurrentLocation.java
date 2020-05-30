package io.wsz.model.location;

import io.wsz.model.content.Content;
import io.wsz.model.content.ContentList;
import io.wsz.model.layer.Layer;
import io.wsz.model.layer.LayersList;
import javafx.beans.property.*;
import javafx.collections.ObservableList;

public class CurrentLocation {
    private static CurrentLocation currentLocation;
    private final ObjectProperty<Location> locationProperty = new SimpleObjectProperty<>();
    private final IntegerProperty currentWidth = new SimpleIntegerProperty();
    private final IntegerProperty currentHeight = new SimpleIntegerProperty();
    private final StringProperty currentName = new SimpleStringProperty();

    public static CurrentLocation get() {
        if (currentLocation == null) {
            currentLocation = new CurrentLocation();
        }
        return currentLocation;
    }

    private CurrentLocation(){
    }

    public ObservableList<Layer> getLayers() {
        return getLocation().getLayers().get();
    }

    public LayersList getLayersList() {
        return getLocation().getLayers();
    }

    public ObservableList<Content> getContent() {
        return getLocation().getContents().get();
    }

    public ContentList getContentList() {
        return getLocation().getContents();
    }

    public void setLocation(Location location) {
        this.currentWidth.set(location.getWidth());
        this.currentHeight.set(location.getHeight());
        this.currentName.set(location.getName());
        this.locationProperty.set(location);
    }

    public Location getLocation() {
        return locationProperty.get();
    }

    public ObjectProperty<Location> locationProperty() {
        return locationProperty;
    }

    public int getCurrentWidth() {
        return currentWidth.get();
    }

    public IntegerProperty currentWidthProperty() {
        return currentWidth;
    }

    public int getCurrentHeight() {
        return currentHeight.get();
    }

    public IntegerProperty currentHeightProperty() {
        return currentHeight;
    }

    public void setCurrentWidth(int currentWidth) {
        this.locationProperty.get().setWidth(currentWidth);
        this.currentWidth.set(currentWidth);
    }

    public void setCurrentHeight(int currentHeight) {
        this.locationProperty.get().setHeight(currentHeight);
        this.currentHeight.set(currentHeight);
    }

    public String getCurrentName() {
        return currentName.get();
    }

    public StringProperty currentNameProperty() {
        return currentName;
    }

    public void setCurrentName(String currentName) {
        this.currentName.set(currentName);
    }
}
