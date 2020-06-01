package io.wsz.model.item;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

import java.util.NoSuchElementException;

public class Asset {
    protected final StringProperty name = new SimpleStringProperty(this, "name");
    protected final ObjectProperty<ItemType> type = new SimpleObjectProperty<>(this, "type");
    protected final StringProperty path = new SimpleStringProperty(this, "path");
    protected final ObjectProperty<Image> image = new SimpleObjectProperty<>(this, "image");

    public Asset(String name, ItemType type, String path) {
        this.name.set(name);
        this.type.set(type);
        this.path.set(path);
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

    public ItemType getType() {
        return type.get();
    }

    public ObjectProperty<ItemType> typeProperty() {
        return type;
    }

    public void setType(ItemType type) {
        this.type.set(type);
    }

    public String getPath() {
        return path.get();
    }

    public StringProperty pathProperty() {
        return path;
    }

    public void setPath(String path) {
        this.path.set(path);
    }

    public Image getImage() {
        if (this.image.get() == null) {
            this.image.set(loadImageFromPath());
            path.addListener((observable, oldValue, newValue) -> {
                this.image.set(loadImageFromPath());
            });
        }
        return image.get();
    }

    private Image loadImageFromPath() {
        if (getPath() == null || getPath().isEmpty()) {
            throw new NoSuchElementException();
        }
        return new Image(getPath());
    }

    public ObjectProperty<Image> imageProperty() {
        return image;
    }

    public void setImage(Image image) {
        this.image.set(image);
    }
}
