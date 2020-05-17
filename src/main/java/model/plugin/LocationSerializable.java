package model.plugin;

import java.io.Serializable;
import java.util.List;

public class LocationSerializable implements Serializable {
    private String name;
    private int width;
    private int height;
    private List<LayerSerializable> layers;
    private List<ContentSerializable> contents;

    public LocationSerializable(String name, int width, int height, List<LayerSerializable> layers, List<ContentSerializable> contents) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.layers = layers;
        this.contents = contents;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public List<LayerSerializable> getLayers() {
        return layers;
    }

    public void setLayers(List<LayerSerializable> layers) {
        this.layers = layers;
    }

    public List<ContentSerializable> getContents() {
        return contents;
    }

    public void setContents(List<ContentSerializable> contents) {
        this.contents = contents;
    }
}
