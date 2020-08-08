package io.wsz.model.stage;

import io.wsz.model.Controller;
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

    public static Image loadImageFromPath(String fileName, String type) {
        if (fileName.isEmpty()) {
            return null;
        }
        String path = getRelativeTypePath(type) + File.separator + fileName;
        File fixedFile = new File(Controller.getProgramDir() + path);
        if (!fixedFile.exists()) {
            try {
                throw new NullPointerException(fixedFile + " does not exist");
            } catch (NullPointerException e) {
                e.printStackTrace();
                return null;
            }
        }
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
                return getResizedImage(url, d, rd);
            }
        }
    }

    private static String getRelativeTypePath(String type) {
        return Sizes.ASSETS_DIR + File.separator + type;
    }

    public static Image getDecreasedQualityWithResolutionImage(Image img) {
        double width = img.getWidth();
        double height = img.getHeight();
        Dimension rd = getRequestedDimension(width, height);
        BufferedImage bInput = SwingFXUtils.fromFXImage(img, null);
        java.awt.Image imgInput = bInput.getScaledInstance(rd.width, rd.height, java.awt.Image.SCALE_DEFAULT);
        BufferedImage bSmall = imageToBufferedImage(imgInput);
        if (Sizes.isResizeWithResolution()) {
            return SwingFXUtils.toFXImage(bSmall, null);
        } else {
            java.awt.Image imgBig = bSmall.getScaledInstance((int) width, (int) height, java.awt.Image.SCALE_SMOOTH);
            BufferedImage bBig = imageToBufferedImage(imgBig);
            return SwingFXUtils.toFXImage(bBig, null);
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
}
