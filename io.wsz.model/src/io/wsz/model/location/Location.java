package io.wsz.model.location;

import io.wsz.model.content.ItemList;
import io.wsz.model.item.PosItem;
import io.wsz.model.layer.LayersList;
import javafx.beans.property.*;

import java.util.ArrayList;
import java.util.List;

public class Location {
    private final StringProperty name = new SimpleStringProperty(this, "name");
    private final IntegerProperty width = new SimpleIntegerProperty(this, "width");
    private final IntegerProperty height = new SimpleIntegerProperty(this, "height");
    private final ObjectProperty<LayersList> layers = new SimpleObjectProperty<>(this, "layers");
    private final ObjectProperty<ItemList> items = new SimpleObjectProperty<>(this, "contents");
    private final List<PosItem> itemsToRemove = new ArrayList<>(0);
    private final List<PosItem> itemsToAdd = new ArrayList<>(0);

    public Location(){}

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

    public int getWidth() {
        return width.get();
    }

    public void setWidth(int width) {
        this.width.set(width);
    }

    public int getHeight() {
        return height.get();
    }

    public void setHeight(int height) {
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
}
