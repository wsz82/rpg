package model.assets;


import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import model.items.ItemType;

import java.util.NoSuchElementException;

public class Asset {
    private final StringProperty name;
    private final ObjectProperty<ItemType> type;
    private final StringProperty path;
    private final ObjectProperty<Image> image;

    public Asset() {
        this.name = new SimpleStringProperty(this, "name");
        this.type = new SimpleObjectProperty<>(this, "type");
        this.path = new SimpleStringProperty(this, "url");
        this.image = new SimpleObjectProperty<>(this, "image");
    }

    public Asset(String name, ItemType type, String path) {
        this.name = new SimpleStringProperty(this, "name");
        setName(name);
        this.type = new SimpleObjectProperty<>(this, "type");
        setType(type);
        this.path = new SimpleStringProperty(this, "url");
        setPath(path);
        this.image = new SimpleObjectProperty<>(this, "image");
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
        }
        return image.get();
    }

    public ObjectProperty<Image> imageProperty() {
        return image;
    }

    private Image loadImageFromPath() {
        if (getPath() == null || getPath().isEmpty()) {
            throw new NoSuchElementException();
        }
        return new Image(getPath());
    }
}
