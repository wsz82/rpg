package editor.view.location;

import editor.view.asset.lists.ObservableItemsList;
import io.wsz.model.item.list.ItemsList;
import io.wsz.model.layer.Layer;
import io.wsz.model.location.Location;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class CurrentObservableLocation {
    private final ObjectProperty<Location> locationProperty = new SimpleObjectProperty<>();
    private final ObservableList<Layer> layers = FXCollections.observableArrayList();
    private final ObservableItemsList itemsList = new ObservableItemsList(true);
    private final DoubleProperty widthProperty = new SimpleDoubleProperty();
    private final DoubleProperty heightProperty = new SimpleDoubleProperty();
    private final StringProperty idProperty = new SimpleStringProperty();

    public CurrentObservableLocation(){}

    public void setLocation(Location location) {
        saveCurrent();
        if (location != null) {
            this.widthProperty.set(location.getWidth());
            this.heightProperty.set(location.getHeight());
            this.idProperty.set(location.getId());
            this.itemsList.clear();
            this.itemsList.addAll(location.getItemsList().getMergedList());
            this.layers.clear();
            this.layers.addAll(location.getLayers());
            this.locationProperty.set(location);
        } else {
            this.widthProperty.set(0);
            this.heightProperty.set(0);
            this.idProperty.set(null);
            this.layers.clear();
            this.itemsList.clear();
            this.locationProperty.set(null);
        }
    }

    public void saveCurrent() {
        Location current = this.locationProperty.get();
        if (current != null) {
            ItemsList items = new ItemsList(true);
            items.fillWith(this.itemsList);
            current.setItemsList(items);
            current.setLayers(new ArrayList<>(layers));
        }
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
        return idProperty.get();
    }

    public StringProperty getIdProperty() {
        return idProperty;
    }

    public void setName(String name) {
        this.locationProperty.get().setId(name);
        this.idProperty.set(name);
    }

    public ObservableItemsList getItemsList() {
        return itemsList;
    }

    public ObservableList<Layer> getLayers() {
        return layers;
    }
}
