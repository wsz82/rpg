package io.wsz.model.item;

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

public class CreatureBase {
    private static final CreatureBase XS_C = new CreatureBase("xs_c.png");
    private static final CreatureBase XS_E = new CreatureBase("xs_e.png");
    private static final CreatureBase XS_N = new CreatureBase("xs_n.png");

    private static final CreatureBase S_C = new CreatureBase("s_c.png");
    private static final CreatureBase S_E = new CreatureBase("s_e.png");
    private static final CreatureBase S_N = new CreatureBase("s_n.png");

    private static final CreatureBase M_C = new CreatureBase("m_c.png");
    private static final CreatureBase M_E = new CreatureBase("m_e.png");
    private static final CreatureBase M_N = new CreatureBase("m_n.png");

    private static final CreatureBase L_C = new CreatureBase("l_c.png");
    private static final CreatureBase L_E = new CreatureBase("l_e.png");
    private static final CreatureBase L_N = new CreatureBase("l_n.png");

    private static final CreatureBase XL_C = new CreatureBase("xl_c.png");
    private static final CreatureBase XL_E = new CreatureBase("xl_e.png");
    private static final CreatureBase XL_N = new CreatureBase("xl_n.png");

    private static final CreatureBase[] bases = new CreatureBase[]{XS_C, XS_E, XS_N, S_C, S_E, S_N, M_C, M_E, M_N,
            L_C, L_E, L_N, XL_C, XL_E, XL_N};

    private Image img;
    private String relativePath;

    public CreatureBase(String path) {
        this.setRelativePath(path);
    }

    public static CreatureBase getCreatureBase(CreatureSize size, CreatureControl control) {
        String path = "";
        path += switch (size) {
            case XS -> "xs";
            case S -> "s";
            case M -> "m";
            case L -> "l";
            case XL -> "xl";
        };
        path += "_";
        path += switch (control) {
            case CONTROL, CONTROLLABLE -> "c";
            case NEUTRAL -> "n";
            case ENEMY -> "e";
        };
        path += ".png";
        for (CreatureBase base : bases) {
            if (base.getRelativePath().equals(path)) return base;
        }
        return null;
    }

    public static CreatureBase[] getBases() {
        return bases;
    }

    public Image getImage() {
        if (img == null) {
            setImg(loadImageFromPath(getRelativePath()));
        }
        return img;
    }

    public void setImg(Image img) {
        this.img = img;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    private Image loadImageFromPath(String fileName) {
        if (fileName.isEmpty()) {
            return null;
        }
        String path = getRelativeTypePath() + File.separator + fileName;
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
                return getChangedImage(url, d, rd);
            }
        }
    }

    private static String getRelativeTypePath() {
        return File.separator + "assets" + File.separator + "circle";
    }

    private Image getChangedImage(String url, Dimension d, Dimension rd) {
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


    private Dimension getRequestedDimension(Dimension d) {
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
}
