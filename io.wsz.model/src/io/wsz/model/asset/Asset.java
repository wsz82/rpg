package io.wsz.model.asset;

import io.wsz.model.Controller;
import io.wsz.model.item.ItemType;
import io.wsz.model.sizes.Sizes;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

import static io.wsz.model.sizes.Sizes.CONSTANT_METER;

public abstract class Asset implements Externalizable {
    private static final long serialVersionUID = 1L;

    private static final String ASSETS_DIR = File.separator + "assets";
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
        return ASSETS_DIR + File.separator + type.toString().toLowerCase();
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
            setImage(loadImageFromPath(getRelativePath()));
        }
        return image.get();
    }

    public Image getImage() {
        if (image.get() == null) {
            setImage(loadImageFromPath(getRelativePath()));
        }
        return image.get();
    }

    protected Image loadImageFromPath(String fileName) {
        String path = getRelativeTypePath(getType()) + File.separator + fileName;
        if (path.isEmpty()) {
            throw new NoSuchElementException();
        }
        File fixedFile = new File(Controller.getProgramDir() + path);
        String url = null;
        try {
            url = fixedFile.toURI().toURL().toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (url == null) {
            return null;
        }

        if (Sizes.getTrueMeter() == CONSTANT_METER) {
            return new Image(url);
        } else {
            Dimension d = getImageDimension(fixedFile);
            if (d == null) {
                throw new NullPointerException(url + " dimension is null");
            }
            Dimension rd = getRequestedDimension(d);

            if (Sizes.isResizeWithResolution()) {
                return new Image(url, rd.width, rd.height, false, false);
            } else {
                return getChangedImage(url, d, rd);
            }
        }
    }

    protected Image getChangedImage(String url, Dimension d, Dimension rd) {
        Image img = new Image(url, rd.width, rd.height, false, false);
        BufferedImage bInput = SwingFXUtils.fromFXImage(img, null);
        java.awt.Image imgInput = bInput.getScaledInstance(d.width, d.height, java.awt.Image.SCALE_DEFAULT);
        BufferedImage bOutput = imageToBufferedImage(imgInput);
        return SwingFXUtils.toFXImage(bOutput ,null);
    }

    private BufferedImage imageToBufferedImage(java.awt.Image img) {
        BufferedImage bi = new BufferedImage
                (img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics bg = bi.getGraphics();
        bg.drawImage(img, 0, 0, null);
        bg.dispose();
        return bi;
    }


    protected Dimension getRequestedDimension(Dimension d) {
        double ratio = (double) Sizes.getTrueMeter() / (double) CONSTANT_METER;
        int rw = (int) (d.width * ratio);
        int rh = (int) (d.height * ratio);
        return new Dimension(rw, rh);
    }

    private Dimension getImageDimension(File input) {
        try(ImageInputStream in = ImageIO.createImageInputStream(input)){
            final Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
            if (readers.hasNext()) {
                ImageReader reader = readers.next();
                try {
                    reader.setInput(in);
                    return new Dimension(reader.getWidth(0), reader.getHeight(0));
                } finally {
                    reader.dispose();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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

        out.writeUTF(name.get());

        out.writeObject(type.get());

        out.writeObject(relativePath.get());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        name.set(in.readUTF());

        type.set((ItemType) in.readObject());

        relativePath.set((String) in.readObject());
    }
}
