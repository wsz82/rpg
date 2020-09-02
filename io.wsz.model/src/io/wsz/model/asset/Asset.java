package io.wsz.model.asset;

import io.wsz.model.Controller;
import io.wsz.model.item.ItemType;
import io.wsz.model.sizes.Paths;
import io.wsz.model.sizes.Sizes;

import java.io.*;
import java.util.Objects;

public abstract class Asset implements Externalizable {
    private static final long serialVersionUID = 1L;

    protected String assetId;
    protected ItemType type;
    protected String path;

    public Asset() {}

    public Asset(ItemType type) {
        this.type = type;
    }

    public Asset(String assetId, ItemType type, String path) {
        this.assetId = assetId;
        this.type = type;
        this.path = path;
    }

    public Asset(Asset other) {
        this.assetId = other.getAssetId();
        this.type = other.getType();
        this.path = other.getPath();
    }

    public String getDir() {
        String path = getPath();
        if (path == null) {
            System.out.println("Null path passed to method");
        }
        return getRelativeTypePath() + path;
    }

    public File getTypeDir(Controller controller) {
        String relativeTypePath = getRelativeTypePath();
        File dir = new File(controller.getProgramDir() + relativeTypePath);
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    public String getRelativeTypePath() {
        return Paths.ASSETS_DIR + File.separator + getAssetDirName();
    }

    protected abstract String getAssetDirName();

    public static String convertToRelativePath(String path) {
        File file = new File(path);
        String fileName = file.getName();
        return File.separator + fileName;
    }

    public String getAssetId() {
        if (assetId == null) {
            return "";
        } else {
            return assetId;
        }
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
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
        return getAssetId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Asset)) return false;
        Asset asset = (Asset) o;
        return Objects.equals(getAssetId(), asset.getAssetId()) &&
                Objects.equals(getType(), asset.getType()) &&
                Objects.equals(getPath(), asset.getPath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAssetId(), getType(), getPath());
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(Sizes.VERSION);

        out.writeObject(assetId);

        out.writeObject(type);

        out.writeObject(path);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        assetId = (String) in.readObject();

        type = (ItemType) in.readObject();

        path = (String) in.readObject();
    }
}
