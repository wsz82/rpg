package io.wsz.model.asset;

import io.wsz.model.Controller;
import io.wsz.model.item.ItemType;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

import java.io.File;
import java.net.MalformedURLException;
import java.util.NoSuchElementException;

public abstract class Asset {
    private static final String ASSETS_DIR = File.separator + "assets";
    protected final StringProperty name = new SimpleStringProperty(this, "name");
    protected final ObjectProperty<ItemType> type = new SimpleObjectProperty<>(this, "type");
    protected final StringProperty relativePath = new SimpleStringProperty(this, "relativePath");
    protected final ObjectProperty<Image> image = new SimpleObjectProperty<>(this, "image");

    public Asset(String name, ItemType type, String relativePath) {
        this.name.set(name);
        this.type.set(type);
        this.relativePath.set(relativePath);
    }

    public static File createAssetTypeDir(ItemType type) {
        String relativeTypePath = getRelativeTypePath(type);
        File dir = new File(Controller.getProgramDir() + relativeTypePath);
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    public static String getRelativeTypePath(ItemType type) {
        return ASSETS_DIR + File.separator + type.toString().toLowerCase();
    }

    public static String convertToRelativeFilePath(String path, ItemType type) {
        File file = new File(path);
        String fileName = file.getName();
        return ASSETS_DIR + File.separator + type.toString().toLowerCase() + File.separator + fileName;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public ItemType getType() {
        return type.get();
    }

    public void setType(ItemType type) {
        this.type.set(type);
    }

    public String getRelativePath() {
        return relativePath.get();
    }

    public void setRelativePath(String relativePath) {
        this.relativePath.set(relativePath);
    }

    public Image getImage() {
        if (this.image.get() == null) {
            setImage(loadImageFromPath());
        }
        return image.get();
    }

    public Image loadImageFromPath() {
        if (getRelativePath() == null || getRelativePath().isEmpty()) {
            throw new NoSuchElementException();
        }
        File fixedFile = new File(Controller.getProgramDir() + getRelativePath());
        String url = null;
        try {
            url = fixedFile.toURI().toURL().toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (url == null) {
            return null;
        }
        return new Image(url, true);
    }

    public void setImage(Image image) {
        this.image.set(image);
    }

    @Override
    public String toString() {
        return getName();
    }
}
