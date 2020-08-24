package io.wsz.model.textures;

import io.wsz.model.sizes.Paths;
import io.wsz.model.stage.ResolutionImage;
import javafx.scene.image.Image;

import java.io.File;

public class Fog {

    private Image image;

    public Fog() {}

    public final Image getImage(File programDir) {
        if (image == null) {
            String path = programDir + Paths.TEXTURES_DIR + Paths.FOG_DIR + Paths.FOG_BASE_NAME_DIR;
            setImage(ResolutionImage.loadImage(path));
        }
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}
