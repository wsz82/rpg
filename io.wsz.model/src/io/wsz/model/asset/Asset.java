package io.wsz.model.asset;

import io.wsz.model.Controller;
import io.wsz.model.item.ItemType;
import io.wsz.model.sizes.Paths;
import io.wsz.model.sizes.Sizes;

import java.io.*;
import java.util.Objects;

public abstract class Asset implements Externalizable {
    private static final long serialVersionUID = 1L;

    protected String name;
    protected ItemType type;
    protected String path;

    public Asset() {}

    public Asset(ItemType type) {
        this.type = type;
    }

    public Asset(String name, ItemType type, String path) {
        this.name = name;
        this.type = type;
        this.path = path;
    }

    public Asset(Asset other) {
        this.name = other.getName();
        this.type = other.getType();
        this.path = other.getPath();
    }

    public String getDir() {
        String path = getPath();
        if (path == null) {
            System.out.println("Null path passed to method");
        }
        ItemType type = getType();
        return getRelativeTypePath(type) + path;
    }

    public static File createAssetTypeDir(ItemType type, Controller controller) {
        String relativeTypePath = getRelativeTypePath(type);
        File dir = new File(controller.getProgramDir() + relativeTypePath);
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    public static String getRelativeTypePath(ItemType type) {
        return Paths.ASSETS_DIR + File.separator + type.toString().toLowerCase();
    }

    public static String convertToRelativePath(String path) {
        File file = new File(path);
        String fileName = file.getName();
        return File.separator + fileName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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
                Objects.equals(getPath(), asset.getPath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getType(), getPath());
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(Sizes.VERSION);

        out.writeObject(name);

        out.writeObject(type);

        out.writeObject(path);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        name = (String) in.readObject();

        type = (ItemType) in.readObject();

        path = (String) in.readObject();
    }
}
