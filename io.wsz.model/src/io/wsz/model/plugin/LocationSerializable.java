package io.wsz.model.plugin;

import java.io.Serializable;
import java.util.List;

public class LocationSerializable implements Serializable {
    private String name;
    private int width;
    private int height;
    private List<LayerSerializable> layers;
    private List<PosItemSerializable> contents;

    public LocationSerializable(String name, int width, int height,
                                List<LayerSerializable> layers, List<PosItemSerializable> contents) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.layers = layers;
        this.contents = contents;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    int getWidth() {
        return width;
    }

    void setWidth(int width) {
        this.width = width;
    }

    int getHeight() {
        return height;
    }

    void setHeight(int height) {
        this.height = height;
    }

    List<LayerSerializable> getLayers() {
        return layers;
    }

    void setLayers(List<LayerSerializable> layers) {
        this.layers = layers;
    }

    List<PosItemSerializable> getItems() {
        return contents;
    }

    void setItems(List<PosItemSerializable> items) {
        this.contents = items;
    }
}
