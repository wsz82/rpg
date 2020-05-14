package model.location;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import model.content.Content;
import model.content.ContentList;
import model.layer.Layer;
import model.layer.LayersList;
import model.stage.CurrentLayer;

public class CurrentLocation {
    private final ObjectProperty<Location> locationProperty = new SimpleObjectProperty<>();
    private final IntegerProperty currentWidth = new SimpleIntegerProperty();
    private final IntegerProperty currentHeight = new SimpleIntegerProperty();
    private static CurrentLocation currentLocation;

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
        this.locationProperty.set(location);
        this.currentWidth.set(location.getWidth());
        this.currentHeight.set(location.getHeight());
        CurrentLayer.setCurrentLayer(0);
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
        this.currentWidth.set(currentWidth);
    }

    public void setCurrentHeight(int currentHeight) {
        this.currentHeight.set(currentHeight);
    }
}
