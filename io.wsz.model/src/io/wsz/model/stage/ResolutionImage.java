package io.wsz.model.stage;

import io.wsz.model.sizes.Sizes;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;

import static io.wsz.model.sizes.Sizes.CONSTANT_METER;

public class ResolutionImage {
    private Image fxImage;
    private double width;
    private double height;

    private static File getCheckedFile(String path) {
        if (path.isEmpty()) {
            throw new IllegalArgumentException("Path is empty");
        }
        return new File(path);
    }

    public ResolutionImage(String path) {
        this(getCheckedFile(path));
    }

    public ResolutionImage(File file) {
        if (file == null) {
            throw new NullPointerException("File is null");
        }
        String name = file.getName();
        if (name.isEmpty() || !name.endsWith(".png")) {
            throw new IllegalArgumentException("File is not PNG");
        }
        if (!file.exists()) {
            throw new NullPointerException(file + " does not exist");
        }
        String url = null;
        try {
            url = file.toURI().toURL().toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (url == null) {
            throw new NullPointerException(name + " URL is null");
        }

        if (Sizes.getTrueMeter() == CONSTANT_METER) {
            this.fxImage = new Image(url);
        } else {
            Dimension d = getImageDimension(file);
            if (d == null) {
                throw new NullPointerException(url + " dimension is null");
            }
            Dimension rd = getRequestedDimension(d);

            if (Sizes.isResizeWithResolution()) {
                this.fxImage = new Image(url, rd.width, rd.height, false, false);
            } else {
                this.fxImage = getResizedImage(url, d, rd);
            }
        }
        this.width = this.fxImage.getWidth();
        this.height = this.fxImage.getHeight();
    }

    private Image getResizedImage(String url, Dimension d, Dimension rd) {
        Image img = new Image(url, rd.width, rd.height, false, false);
        BufferedImage bInput = SwingFXUtils.fromFXImage(img, null);
        java.awt.Image imgInput = bInput.getScaledInstance(d.width, d.height, java.awt.Image.SCALE_DEFAULT);
        BufferedImage bOutput = imageToBufferedImage(imgInput);
        return SwingFXUtils.toFXImage(bOutput ,null);
    }

    public ResolutionImage(File file, int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Provided dimensions must be greater than 0");
        }
        String name = file.getName();
        String url = null;
        try {
            url = file.toURI().toURL().toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (url == null) {
            throw new NullPointerException(name + " URL is null");
        }

        if (Sizes.getTrueMeter() == CONSTANT_METER) {
            this.fxImage = new Image(url, width, height, false, false, false);
        } else {
            Dimension d = new Dimension(width, height);
            Dimension rd = getRequestedDimension(d);
            this.fxImage = getResizedImage(url, d, rd);
        }
        this.width = this.fxImage.getWidth();
        this.height = this.fxImage.getHeight();
    }

    private BufferedImage imageToBufferedImage(java.awt.Image img) {
        BufferedImage bi = new BufferedImage
                (img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics bg = bi.getGraphics();
        bg.drawImage(img, 0, 0, null);
        bg.dispose();
        return bi;
    }

    private Dimension getRequestedDimension(Dimension d) {
        return getRequestedDimension(d.width, d.height);
    }

    private Dimension getRequestedDimension(double width, double height) {
        double ratio = (double) Sizes.getTrueMeter() / (double) CONSTANT_METER;
        int rw = (int) (width * ratio);
        int rh = (int) (height * ratio);
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

    public Image getFxImage() {
        return fxImage;
    }

    public void setFxImage(Image fxImage) {
        this.fxImage = fxImage;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }
}
