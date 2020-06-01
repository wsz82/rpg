package io.wsz.model.location;

import io.wsz.model.content.Content;
import io.wsz.model.content.ContentList;
import io.wsz.model.layer.Layer;
import io.wsz.model.layer.LayersList;
import javafx.beans.property.*;
import javafx.collections.ObservableList;

public class CurrentLocation {
    private static CurrentLocation singleton;
    private final ObjectProperty<Location> locationProperty = new SimpleObjectProperty<>();
    private final IntegerProperty width = new SimpleIntegerProperty();
    private final IntegerProperty height = new SimpleIntegerProperty();
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

    public ObservableList<Content> getContent() {
        return getLocation().getContents().get();
    }

    public ContentList getContentList() {
        return getLocation().getContents();
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

    public int getWidth() {
        return width.get();
    }

    public IntegerProperty widthProperty() {
        return width;
    }

    public int getHeight() {
        return height.get();
    }

    public IntegerProperty heightProperty() {
        return height;
    }

    public void setWidth(int width) {
        this.locationProperty.get().setWidth(width);
        this.width.set(width);
    }

    public void setHeight(int height) {
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
