package model.location;

import javafx.beans.property.*;
import model.content.ContentList;
import model.layer.LayersList;

public class Location {
    private final StringProperty name = new SimpleStringProperty(this, "name");
    private final IntegerProperty width = new SimpleIntegerProperty(this, "width");
    private final IntegerProperty height = new SimpleIntegerProperty(this, "height");
    private final ObjectProperty<LayersList> layers = new SimpleObjectProperty<>(this, "layers");
    private final ObjectProperty<ContentList> contents = new SimpleObjectProperty<>(this, "contents");

    public Location(){}

    public Location(String name) {
        this.name.set(name);
        this.layers.set(new LayersList());
        this.contents.set(new ContentList());
    }

    public Location(String name, int width, int height) {
        this.name.set(name);
        this.width.set(width);
        this.height.set(height);
        this.layers.set(new LayersList());
        this.contents.set(new ContentList());
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

    public int getWidth() {
        return width.get();
    }

    public IntegerProperty widthProperty() {
        return width;
    }

    public void setWidth(int width) {
        this.width.set(width);
    }

    public int getHeight() {
        return height.get();
    }

    public IntegerProperty heightProperty() {
        return height;
    }

    public void setHeight(int height) {
        this.height.set(height);
    }

    public LayersList getLayers() {
        return layers.get();
    }

    public ObjectProperty<LayersList> layersProperty() {
        return layers;
    }

    public void setLayers(LayersList layers) {
        this.layers.set(layers);
    }

    public ContentList getContents() {
        return contents.get();
    }

    public ObjectProperty<ContentList> contentsProperty() {
        return contents;
    }

    public void setContents(ContentList contents) {
        this.contents.set(contents);
    }
}
