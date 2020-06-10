package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.stage.Coords;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;
import java.util.NoSuchElementException;

public abstract class Asset {
    private static final String ASSETS_DIR = File.separator + "assets";
    protected final StringProperty name = new SimpleStringProperty(this, "name");
    protected final ObjectProperty<ItemType> type = new SimpleObjectProperty<>(this, "type");
    protected final StringProperty relativePath = new SimpleStringProperty(this, "relativePath");
    protected final ObjectProperty<Image> image = new SimpleObjectProperty<>(this, "image");
    protected volatile List<Coords> coverLine;
    protected volatile List<List<Coords>> collisionPolygons;

    public Asset(String name, ItemType type, String relativePath,
                 List<Coords> coverLine, List<List<Coords>> collisionPolygons) {
        this.name.set(name);
        this.type.set(type);
        this.relativePath.set(relativePath);
        this.coverLine = coverLine;
        this.collisionPolygons = collisionPolygons;
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

    public String getRelativePath() {
        return relativePath.get();
    }

    public StringProperty relativePathProperty() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath.set(relativePath);
        if (this instanceof PosItem) {
            if (((PosItem) this).getPos() != null) {
                return;
            }
            Controller.get().getLocationsList().forEach(l -> {
                l.getContents().get().stream()
                        .filter(c -> c.getItem().getName().equals(getName()))
                        .filter(c -> c.getItem().isGeneric())
                        .forEach(c -> c.getItem().setRelativePath(relativePath));
            });
        }
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
        return new Image(url);
    }

    public ObjectProperty<Image> imageProperty() {
        return image;
    }

    public void setImage(Image image) {
        this.image.set(image);
        if (this instanceof PosItem) {
            if (((PosItem) this).getPos() != null) {
                return;
            }
            Controller.get().getLocationsList().forEach(l -> {
                l.getContents().get().stream()
                        .filter(c -> c.getItem().getName().equals(getName()))
                        .filter(c -> c.getItem().isGeneric())
                        .forEach(c -> c.getItem().setImage(image));
            });
        }
    }

    public List<Coords> getCoverLine() {
        return coverLine;
    }

    public void setCoverLine(List<Coords> coverLine) {
        this.coverLine = coverLine;
        if (this instanceof PosItem) {
            if (((PosItem) this).getPos() != null) {
                return;
            }
            Controller.get().getLocationsList().forEach(l -> {
                l.getContents().get().stream()
                        .filter(c -> c.getItem().getName().equals(getName()))
                        .filter(c -> c.getItem().isGeneric())
                        .forEach(c -> c.getItem().setCoverLine(coverLine));
            });
        }
    }

    public List<List<Coords>> getCollisionPolygons() {
        return collisionPolygons;
    }

    public void setCollisionPolygons(List<List<Coords>> collisionPolygons) {
        this.collisionPolygons = collisionPolygons;
        if (this instanceof PosItem) {
            if (((PosItem) this).getPos() != null) {
                return;
            }
            Controller.get().getLocationsList().forEach(l -> {
                l.getContents().get().stream()
                        .filter(c -> c.getItem().getName().equals(getName()))
                        .filter(c -> c.getItem().isGeneric())
                        .forEach(c -> c.getItem().setCollisionPolygons(collisionPolygons));
            });
        }
    }

    @Override
    public String toString() {
        return getName();
    }
}
