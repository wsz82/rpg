package io.wsz.model.plugin;

import java.io.Serializable;
import java.util.List;

public class LocationSerializable implements Serializable {
    private String name;
    private double width;
    private double height;
    private List<LayerSerializable> layers;
    private List<PosItemSerializable> contents;

    public LocationSerializable(String name, double width, double height,
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

    double getWidth() {
        return width;
    }

    void setWidth(double width) {
        this.width = width;
    }

    double getHeight() {
        return height;
    }

    void setHeight(double height) {
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
