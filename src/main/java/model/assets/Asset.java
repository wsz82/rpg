package model.assets;


import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import model.items.ItemType;

public class Asset {
    private final StringProperty name;
    private final ObjectProperty<ItemType> type;
    private final StringProperty url;
    private final ObjectProperty<Image> image;

    public Asset() {
        this.name = new SimpleStringProperty(this, "name");
        this.type = new SimpleObjectProperty<>(this, "type");
        this.url = new SimpleStringProperty(this, "url");
        this.image = new SimpleObjectProperty<>(this, "image");
    }

    public Asset(String name, ItemType type, String url) {
        this.name = new SimpleStringProperty(this, "name");
        setName(name);
        this.type = new SimpleObjectProperty<>(this, "type");
        setType(type);
        this.url = new SimpleStringProperty(this, "url");
        setUrl(url);
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

    public String getUrl() {
        return url.get();
    }

    public StringProperty urlProperty() {
        return url;
    }

    public void setUrl(String url) {
        this.url.set(url);
    }

    public Image getImage() {
        return image.get();
    }

    public ObjectProperty<Image> imageProperty() {
        return image;
    }

    public void setImage(Image image) {
        this.image.set(image);
    }
}
