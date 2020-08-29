package io.wsz.model.location;

import io.wsz.model.item.PosItem;
import io.wsz.model.layer.Layer;
import io.wsz.model.sizes.Sizes;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Location implements Externalizable {
    private static final long serialVersionUID = 1L;

    private final StringProperty name = new SimpleStringProperty(this, "name");
    private final DoubleProperty width = new SimpleDoubleProperty(this, "width");
    private final DoubleProperty height = new SimpleDoubleProperty(this, "height");
    private final ObjectProperty<ObservableList<Layer>> layers = new SimpleObjectProperty<>(this, "layers");
    private final ObjectProperty<ObservableList<PosItem>> items = new SimpleObjectProperty<>(this, "contents");
    private final List<PosItem> itemsToRemove = new ArrayList<>(0);
    private final List<PosItem> itemsToAdd = new ArrayList<>(0);

    private List<List<FogStatusWithImage>> discoveredFog;

    public Location() {}

    public Location(String name) {
        this.name.set(name);
        this.layers.set(FXCollections.observableArrayList());
        this.items.set(FXCollections.observableArrayList());
    }

    public Location(String name, int width, int height) {
        this.name.set(name);
        this.width.set(width);
        this.height.set(height);
        this.layers.set(FXCollections.observableArrayList());
        this.items.set(FXCollections.observableArrayList());
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public double getWidth() {
        return width.get();
    }

    public void setWidth(double width) {
        this.width.set(width);
    }

    public double getHeight() {
        return height.get();
    }

    public void setHeight(double height) {
        this.height.set(height);
    }

    public ObservableList<Layer> getLayers() {
        return layers.get();
    }

    public void setLayers(ObservableList<Layer> layers) {
        this.layers.set(layers);
    }

    public ObservableList<PosItem> getItems() {
        return items.get();
    }

    public void setItems(ObservableList<PosItem> items) {
        this.items.set(items);
    }

    public List<PosItem> getItemsToRemove() {
        return itemsToRemove;
    }

    public List<PosItem> getItemsToAdd() {
        return itemsToAdd;
    }

    public List<List<FogStatusWithImage>> getDiscoveredFog() {
        return discoveredFog;
    }

    public void setDiscoveredFog(List<List<FogStatusWithImage>> discoveredFog) {
        this.discoveredFog = discoveredFog;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;
        Location location = (Location) o;
        return Objects.equals(getName(), location.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(Sizes.VERSION);

        out.writeUTF(name.get());

        out.writeDouble(width.get());

        out.writeDouble(height.get());

        out.writeObject(new ArrayList<>(layers.get()));

        out.writeObject(new ArrayList<>(items.get()));

        out.writeObject(itemsToRemove);

        out.writeObject(itemsToAdd);

        out.writeObject(discoveredFog);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        name.set(in.readUTF());

        width.set(in.readDouble());

        height.set(in.readDouble());

        List<Layer> serLayers = (List<Layer>) in.readObject();
        ObservableList<Layer> observableLayers = FXCollections.observableArrayList(serLayers);
        layers.set(observableLayers);

        List<PosItem> serItems = (List<PosItem>) in.readObject();
        ObservableList<PosItem> observableItems = FXCollections.observableArrayList(serItems);
        items.set(observableItems);

        itemsToRemove.addAll((List<PosItem>) in.readObject());

        itemsToAdd.addAll((List<PosItem>) in.readObject());

        discoveredFog = (List<List<FogStatusWithImage>>) in.readObject();
    }
}
