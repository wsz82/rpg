package io.wsz.model.textures;

import io.wsz.model.sizes.Paths;
import io.wsz.model.stage.ResolutionImage;
import javafx.scene.image.Image;

import java.io.File;

public class Fog {
    private static final String FOG_DIR = File.separator + "fog";
    private static final String FOG_BASE_NAME = "fog.png";
    private static final String FOG_BASE_NAME_DIR = File.separator + FOG_BASE_NAME;

    private Image image;

    public Fog() {}

    public final Image getImage(File programDir) {
        if (image == null) {
            String path = programDir + Paths.TEXTURES_DIR + FOG_DIR + FOG_BASE_NAME_DIR;
            setImage(ResolutionImage.loadImage(path));
        }
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}
