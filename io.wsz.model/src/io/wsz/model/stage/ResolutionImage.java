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

    public static Image loadImage(String path) {
        if (path.isEmpty() || !path.endsWith(".png")) {
            throw new IllegalArgumentException("File is not PNG");
        }
        File file = new File(path);
        return loadImage(file);
    }

    public static Image loadImage(File file) {
        if (file == null) {
            throw new NullPointerException("File is null");
        }
        String name = file.getName();
        if (name.isEmpty() || !name.endsWith(".png")) {
            throw new IllegalArgumentException("File is not PNG");
        }
        if (!file.exists()) {
            try {
                throw new NullPointerException(file + " does not exist");
            } catch (NullPointerException e) {
                e.printStackTrace();
                return null;
            }
        }
        String url = null;
        try {
            url = file.toURI().toURL().toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (url == null) {
            return null;
        }

        if (Sizes.getTrueMeter() == CONSTANT_METER) {
            return new Image(url);
        } else {
            Dimension d = getImageDimension(file);
            if (d == null) {
                throw new NullPointerException(url + " dimension is null");
            }
            Dimension rd = getRequestedDimension(d);

            if (Sizes.isResizeWithResolution()) {
                return new Image(url, rd.width, rd.height, false, false);
            } else {
                return getResizedImage(url, d, rd);
            }
        }
    }

    public static Image getResizedImage(String url, Dimension d, Dimension rd) {
        Image img = new Image(url, rd.width, rd.height, false, false);
        BufferedImage bInput = SwingFXUtils.fromFXImage(img, null);
        java.awt.Image imgInput = bInput.getScaledInstance(d.width, d.height, java.awt.Image.SCALE_DEFAULT);
        BufferedImage bOutput = imageToBufferedImage(imgInput);
        return SwingFXUtils.toFXImage(bOutput ,null);
    }

    private static BufferedImage imageToBufferedImage(java.awt.Image img) {
        BufferedImage bi = new BufferedImage
                (img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics bg = bi.getGraphics();
        bg.drawImage(img, 0, 0, null);
        bg.dispose();
        return bi;
    }

    public static Dimension getRequestedDimension(Dimension d) {
        return getRequestedDimension(d.width, d.height);
    }

    public static Dimension getRequestedDimension(double width, double height) {
        double ratio = (double) Sizes.getTrueMeter() / (double) CONSTANT_METER;
        int rw = (int) (width * ratio);
        int rh = (int) (height * ratio);
        return new Dimension(rw, rh);
    }

    private static Dimension getImageDimension(File input) {
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

    public static Image loadDefinedDimensionImage(File file, int width, int height) {
        String url = null;
        try {
            url = file.toURI().toURL().toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (url == null) {
            return null;
        }


        if (width == 0 || height == 0) {
            return null;
        }

        if (Sizes.getTrueMeter() == CONSTANT_METER) {
            return new Image(url, width, height, false, false, false);
        } else {
            Dimension d = new Dimension(width, height);
            Dimension rd = getRequestedDimension(d);
            return getResizedImage(url, d, rd);
        }
    }
}
