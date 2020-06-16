package io.wsz.model.location;

import io.wsz.model.content.ItemList;
import io.wsz.model.item.PosItem;
import io.wsz.model.layer.LayersList;
import javafx.beans.property.*;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

public class Location implements Externalizable {
    private final StringProperty name = new SimpleStringProperty(this, "name");
    private final DoubleProperty width = new SimpleDoubleProperty(this, "width");
    private final DoubleProperty height = new SimpleDoubleProperty(this, "height");
    private final ObjectProperty<LayersList> layers = new SimpleObjectProperty<>(this, "layers");
    private final ObjectProperty<ItemList> items = new SimpleObjectProperty<>(this, "contents");
    private final List<PosItem> itemsToRemove = new ArrayList<>(0);
    private final List<PosItem> itemsToAdd = new ArrayList<>(0);

    public Location() {}

    public Location(String name) {
        this.name.set(name);
        this.layers.set(new LayersList());
        this.items.set(new ItemList());
    }

    public Location(String name, int width, int height) {
        this.name.set(name);
        this.width.set(width);
        this.height.set(height);
        this.layers.set(new LayersList());
        this.items.set(new ItemList());
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

    public LayersList getLayers() {
        return layers.get();
    }

    public void setLayers(LayersList layers) {
        this.layers.set(layers);
    }

    public ItemList getItems() {
        return items.get();
    }

    public void setItems(ItemList items) {
        this.items.set(items);
    }

    public List<PosItem> getItemsToRemove() {
        return itemsToRemove;
    }

    public List<PosItem> getItemsToAdd() {
        return itemsToAdd;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(name.get());

        out.writeDouble(width.get());

        out.writeDouble(height.get());

        out.writeObject(layers.get());

        out.writeObject(items.get());

        out.writeObject(itemsToRemove);

        out.writeObject(itemsToAdd);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        name.set(in.readUTF());

        width.set(in.readDouble());

        height.set(in.readDouble());

        layers.set((LayersList) in.readObject());

        items.set((ItemList) in.readObject());

        itemsToRemove.addAll((List<PosItem>) in.readObject());

        itemsToAdd.addAll((List<PosItem>) in.readObject());
    }
}
