package io.wsz.model.asset;

import io.wsz.model.Controller;
import io.wsz.model.item.ItemType;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.ResolutionImage;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

import java.io.*;
import java.util.Objects;

public abstract class Asset implements Externalizable {
    private static final long serialVersionUID = 1L;

    protected final StringProperty name = new SimpleStringProperty(this, "name");
    protected final ObjectProperty<ItemType> type = new SimpleObjectProperty<>(this, "type");
    protected final StringProperty relativePath = new SimpleStringProperty(this, "relativePath");
    protected final ObjectProperty<Image> image = new SimpleObjectProperty<>(this, "image");

    public Asset() {}

    public Asset(ItemType type) {
        this.type.set(type);
    }

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
        return Sizes.ASSETS_DIR + File.separator + type.toString().toLowerCase();
    }

    public static String convertToRelativePath(String path) {
        File file = new File(path);
        String fileName = file.getName();
        return File.separator + fileName;
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

    public final Image getInitialImage() {
        if (image.get() == null) {
            setImage(ResolutionImage.loadImageFromPath(getRelativePath(), getType().toString().toLowerCase()));
        }
        return image.get();
    }

    public Image getImage() {
        if (image.get() == null) {
            setImage(ResolutionImage.loadImageFromPath(getRelativePath(), getType().toString().toLowerCase()));
        }
        return image.get();
    }

    public double getImageHeight() {
        Image img = getImage();
        if (img == null) return 0;
        return img.getHeight() / Sizes.getMeter();
    }

    public double getImageWidth() {
        Image img = getImage();
        if (img == null) return 0;
        return img.getWidth() / Sizes.getMeter();
    }

    public void setImage(Image image) {
        this.image.set(image);
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Asset)) return false;
        Asset asset = (Asset) o;
        return Objects.equals(getName(), asset.getName()) &&
                Objects.equals(getType(), asset.getType()) &&
                Objects.equals(getRelativePath(), asset.getRelativePath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getType(), getRelativePath());
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(Sizes.VERSION);

        String name = this.name.get();
        if (name == null) name = "";
        out.writeUTF(name);

        out.writeObject(type.get());

        out.writeObject(relativePath.get());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        String name = in.readUTF();
        if (name.isEmpty()) name = null;
        this.name.set(name);

        type.set((ItemType) in.readObject());

        relativePath.set((String) in.readObject());
    }
}
